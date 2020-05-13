;;; https://adventofcode.com/2018/day/9
(ns data.deque.perf
  (:require [taoensso.tufte :as tufte :refer [defnp p profiled profile]]))

(defprotocol ICircle
  (ccw [_])
  (cw [_])
  (circle-push [_ v])
  (circle-pop [_])
  (circle-peek [_]))

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


(defn benchmark [ctor name]
  (tufte/add-basic-println-handler! {})

  (profile
    {}
    (dotimes [_ 3]
      (assert (= 386151 (p (str name ":small") (play (ctor 0) 459 71790)))))
    (dotimes [_ 2]
      (assert (= 32700280 (p (str name ":medium") (play (ctor 0) 459 717900)))))
    (dotimes [_ 1]
      (assert (= 3211264152 (p (str name ":large") (play (ctor 0) 459 7179000)))))))
