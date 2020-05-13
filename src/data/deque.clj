(ns data.deque
  (:require [data.finger-tree :refer [<| |> viewl viewr peekl peekr empty-tree]])
  (:import (clojure.lang IPersistentStack IPersistentCollection Seqable ISeq Sequential Counted IObj IHashEq)))

;(set! *warn-on-reflection* true)

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(declare EMPTY)

(deftype PersistentDequeSeq [_meta tree]
  ISeq
  (first [_]
    (peekl tree))
  (more [this]
    (let [[_ tree'] (viewl tree)]
      (if (identical? tree' empty-tree)
        (empty this)
        (PersistentDequeSeq. _meta tree'))))
  (next [_]
    (let [[_ tree'] (viewl tree)]
      (if-not (identical? tree' empty-tree)
        (PersistentDequeSeq. _meta tree'))))
  (cons [_ v]
    (PersistentDequeSeq. _meta (<| tree v)))

  Sequential
  IPersistentCollection
  (empty [_]
    (with-meta clojure.lang.PersistentList/EMPTY _meta))
  (equiv [this other]
    ;; copied from cljs.core
    (boolean
      (when (sequential? other)
        (loop [xs this ys (seq other)]
          (cond (nil? xs) (nil? ys)
                (nil? ys) false
                (= (first xs) (first ys)) (recur (next xs) (next ys))
                :else false)))))

  Seqable
  (seq [this] this)

  IHashEq
  (hasheq [this] (hash-ordered-coll this)))

(deftype PersistentDeque [_meta count tree]
  IDeque
  (add-first [_ v] (PersistentDeque. _meta (inc count) (<| tree v)))
  (add-last [_ v] (PersistentDeque. _meta (inc count) (|> tree v)))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_]
    (let [[_ tree'] (viewl tree)]
      (PersistentDeque. _meta (max (dec count) 0) tree')))
  (remove-last [_]
    (let [[_ tree'] (viewr tree)]
      (PersistentDeque. _meta (max (dec count) 0) tree')))

  IObj
  (meta [_] _meta)
  (withMeta [coll new-meta]
    (if (identical? new-meta _meta)
      coll
      (PersistentDeque. new-meta count tree)))

  IHashEq
  (hasheq [this] (hash-ordered-coll this))

  IPersistentStack
  (peek [this] (peek-last this))
  (pop [this] (remove-last this))

  Sequential
  Seqable
  (seq [_]
    (if-not (identical? empty-tree tree)
      (PersistentDequeSeq. nil tree)))

  IPersistentCollection
  (empty [_] (with-meta EMPTY _meta))
  (equiv [coll other]
    (and (sequential? other) (= (seq coll) (seq other))))
  (cons [coll v] (add-first coll v))

  Counted
  (count [_] count))

(def EMPTY (PersistentDeque. nil 0 empty-tree))

(defn deque [& coll]
  (into EMPTY coll))
