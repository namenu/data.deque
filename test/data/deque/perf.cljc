;;; https://adventofcode.com/2018/day/9
(ns data.deque.perf
  (:require [data.deque :as dq])
  #?(:clj (:import (data.deque PersistentDeque))))

(defprotocol ICircle
  (ccw [_])
  (cw [_])
  (circle-push [_ v])
  (circle-pop [_])
  (circle-peek [_]))

(extend-type #?(:cljs dq/PersistentDeque
                :clj  PersistentDeque)
  ICircle
  (ccw [circle]
    (let [v (peek circle)]
      (-> circle (dq/remove-last) (dq/add-first v))))
  (cw [circle]
    (let [v (first circle)]
      (-> circle (dq/remove-first) (dq/add-last v))))
  (circle-push [circle v]
    (dq/add-last circle v))
  (circle-pop [circle]
    (pop circle))
  (circle-peek [circle]
    (peek circle)))

(defn place [circle next-num]
  (if (zero? (mod next-num 23))
    (let [circle   (nth (iterate ccw circle) 7)
          score-at (circle-peek circle)]
      [(-> circle (circle-pop) (cw)) (+ score-at next-num)])
    [(-> circle (cw) (circle-push next-num)) 0]))

(defn play [circle num-players last-marble]
  (loop [circle    circle
         marble    1
         score-map {}]
    (if (> marble last-marble)
      (->> score-map (apply max-key val) (second))
      (let [[circle score] (place circle marble)
            player (mod marble num-players)]
        (recur circle (inc marble) (update score-map player (fnil + 0) score))))))

(comment
  (require '[taoensso.tufte :as tufte :refer [defnp p profiled profile]])

  (tufte/add-basic-println-handler! {})

  (profile
    {}
    (dotimes [_ 5]
      (p :small (play (dq/deque 0) 459 71790)))
    (dotimes [_ 4]
      (p :medium (play (dq/deque 0) 459 717900)))
    (dotimes [_ 3]
      (p :large (play (dq/deque 0) 459 7179000)))))

