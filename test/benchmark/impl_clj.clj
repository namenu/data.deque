(ns benchmark.impl-clj
  (:require [benchmark.core :refer [ICircle benchmark]]
            [data.deque :as dq]
            [clojure.data.finger-tree :refer [double-list conjl]])
  (:import (data.deque PersistentDeque)
           (java.util ArrayDeque)
           (clojure.data.finger_tree DoubleList)))

(extend-type PersistentDeque
  ICircle
  (ccw [circle]
    (let [v (dq/peek-last circle)]
      (-> circle (dq/remove-last) (dq/add-first v))))
  (cw [circle]
    (let [v (dq/peek-first circle)]
      (-> circle (dq/remove-first) (dq/add-last v))))
  (circle-push [circle v]
    (dq/add-last circle v))
  (circle-pop [circle]
    (dq/remove-last circle))
  (circle-peek [circle]
    (dq/peek-last circle)))

(extend-type ArrayDeque
  ICircle
  (ccw [circle]
    (let [v (.removeLast circle)]
      (.addFirst circle v) circle))
  (cw [circle]
    (let [v (.removeFirst circle)]
      (.addLast circle v) circle))
  (circle-push [circle v]
    (.addLast circle v) circle)
  (circle-pop [circle]
    (.removeLast circle) circle)
  (circle-peek [circle]
    (.peekLast circle)))

(extend-type DoubleList
  ICircle
  (ccw [circle]
    (let [v (peek circle)]
      (-> circle (pop) (conjl v))))
  (cw [circle]
    (let [v (first circle)]
      (-> circle (rest) (conj v))))
  (circle-push [circle v]
    (conj circle v))
  (circle-pop [circle]
    (pop circle))
  (circle-peek [circle]
    (peek circle)))

(core #(dq/deque %1) "data.deque")
(core #(ArrayDeque. [%1]) "java.util.ArrayDeque")
(core #(double-list %1) "clojure.data.finger-tree")
