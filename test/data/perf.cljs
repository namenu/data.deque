;;; https://adventofcode.com/2018/day/9
(ns data.perf
  (:require [cljs.test :refer-macros [deftest testing is run-tests]]
            [data.deque :as dq]))

(defprotocol ICircle
  (ccw [_])
  (cw [_])
  (circle-push [_ v])
  (circle-pop [_])
  (circle-peek [_]))

(extend-type dq/PersistentDeque
  ICircle
  (ccw [circle]
    (let [v (peek circle)]
      (-> circle (pop) (dq/add-first v))))
  (cw [circle]
    (let [v (first circle)]
      (-> circle (rest) (conj v))))
  (circle-push [circle v]
    (conj circle v))
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


(deftest test-day8
  (testing "finger-tree impl"
    (is (= 386151 (time (play (dq/deque 0) 459 71790))))
    (is (= 3211264152 (time (play (dq/deque 0) 459 7179000))))
    "Elapsed time: 1237.000000 msecs"
    "Elapsed time: 99522.000000 msecs"))

;(run-tests)
