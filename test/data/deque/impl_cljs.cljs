(ns data.deque.impl-cljs
  (:require [data.deque :as dq]
            [data.deque.perf :refer [benchmark]]))

(extend-type dq/PersistentDeque
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

(benchmark #(dq/deque %1) "data.deque")
