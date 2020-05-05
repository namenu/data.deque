(ns data.tests
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [data.deque :refer [empty-deque deque add-first add-last remove-first remove-last peek-first peek-last]]))

(deftest Remove-From-Empty-Deques
  (is (= () (remove-first empty-deque)))
  (is (= () (remove-last empty-deque)))
  (is (= () (pop empty-deque))))

(deftest Get-Empty-Deques
  (is (nil? (first empty-deque)))
  (is (nil? (peek-first empty-deque)))
  (is (nil? (peek-last empty-deque))))

(deftest empty-test
  (let [non-empty (conj empty-deque 10)]
    (is (empty? (empty non-empty)))))

(deftest equiv-test
  (is (= (add-last (deque 0) 1)
         (add-first (deque 1) 0))))

(run-tests)
