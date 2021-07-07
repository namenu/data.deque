(ns data.deque
  (:require [data.finger-tree :refer [<| |> viewl viewr peekl peekr empty-tree]])
  (:import (clojure.lang IPersistentStack IPersistentCollection Seqable ISeq Sequential Counted IObj IHashEq PersistentList)))

;(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

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
    (with-meta PersistentList/EMPTY _meta))
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
  (hasheq [this]
    (reduce (fn [acc e] (unchecked-add-int
                          (unchecked-multiply-int 31 acc)
                          (hash e)))
            1
            this))

  Comparable
  (compareTo [this that]
    (loop [xs this ys (seq that)]
      (cond (nil? xs) (if (nil? ys) 0 -1)
            (nil? ys) 1
            :else (let [cmp (compare (first xs) (first ys))]
                    (if (zero? cmp)
                      (recur (next xs) (next ys))
                      cmp))))))

(deftype PersistentDeque [_meta cnt tree]
  IDeque
  (add-first [_ v] (PersistentDeque. _meta (inc cnt) (<| tree v)))
  (add-last [_ v] (PersistentDeque. _meta (inc cnt) (|> tree v)))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_]
    (let [[_ tree'] (viewl tree)]
      (PersistentDeque. _meta (max (dec cnt) 0) tree')))
  (remove-last [_]
    (let [[_ tree'] (viewr tree)]
      (PersistentDeque. _meta (max (dec cnt) 0) tree')))

  IObj
  (meta [_] _meta)
  (withMeta [coll new-meta]
    (if (identical? new-meta _meta)
      coll
      (PersistentDeque. new-meta cnt tree)))

  IHashEq
  (hasheq [this]
    (-> (hash (seq this))
        (mix-collection-hash cnt)))

  Comparable
  (compareTo [this that]
    (cond
      (< cnt (count that)) -1
      (> cnt (count that)) 1
      (== cnt 0) 0
      :else (compare (seq this) (seq that))))

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
  (count [_] cnt))

(def EMPTY (PersistentDeque. nil 0 empty-tree))

(defn deque
  "Creates a new deque containing the args."
  [& coll]
  (into EMPTY coll))
