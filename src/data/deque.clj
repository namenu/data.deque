(ns data.deque
  (:require [data.finger-tree :refer [<| |> viewl viewr peekl peekr empty-tree]])
  (:import (clojure.lang IPersistentStack IPersistentCollection PersistentQueue Seqable ISeq Sequential PersistentHashMap Counted IObj)))

;(set! *warn-on-reflection* true)

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(declare EMPTY)

(deftype ^:private PersistentDequeSeq [meta tree]
  ISeq
  (first [_]
    (peekl tree))
  (more [this]
    (let [tree' (second (viewl tree))]
      (if (identical? tree' empty-tree)
        (empty this)
        (PersistentDequeSeq. meta tree'))))
  (next [_]
    (let [tree' (second (viewl tree))]
      (if-not (identical? tree' empty-tree)
        (PersistentDequeSeq. meta tree'))))
  (cons [_ v]
    (PersistentDequeSeq. meta (<| tree v)))

  Sequential
  IPersistentCollection
  (empty [_]
    (with-meta clojure.lang.PersistentList/EMPTY meta))
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
  (seq [this] this))

(deftype PersistentDeque [meta count tree]
  IDeque
  (add-first [_ v] (PersistentDeque. meta (inc count) (<| tree v)))
  (add-last [_ v] (PersistentDeque. meta (inc count) (|> tree v)))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_] (PersistentDeque. meta (max (dec count) 0) (second (viewl tree))))
  (remove-last [_] (PersistentDeque. meta (max (dec count) 0) (second (viewr tree))))

  IObj
  (meta [_] meta)
  (withMeta [coll new-meta]
    (if (identical? new-meta meta)
      coll
      (PersistentDeque. new-meta count tree)))

  IPersistentStack
  (peek [this] (peek-last this))
  (pop [this] (remove-last this))

  Sequential
  Seqable
  (seq [_]
    (if-not (identical? empty-tree tree)
      (PersistentDequeSeq. nil tree)))

  IPersistentCollection
  (empty [_] (with-meta EMPTY meta))
  (equiv [coll other]
    (and (sequential? other) (= (seq coll) (seq other))))
  (cons [coll v] (add-first coll v))

  Counted
  (count [_] count))

(def EMPTY (PersistentDeque. nil 0 empty-tree))

(defn deque [& coll]
  (into EMPTY coll))
