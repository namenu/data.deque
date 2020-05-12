(ns data.deque.tests
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing run-tests]]
               :clj  [clojure.test :refer [deftest is testing run-tests]])
            [data.deque :refer [deque add-first add-last remove-first remove-last peek-first peek-last]]
            [data.finger-tree :as ft]))

(def empty-deque
  #?(:cljs (.-EMPTY data.deque.PersistentDeque)
     :clj  data.deque/EMPTY))

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
    (is (empty? (empty non-empty)))
    (is (empty? (empty (seq non-empty))))))

(deftest equiv-test
  (is (= (add-last (deque 0) 1)
         (add-first (deque 1) 0)))
  (is (= empty-deque ()))
  (is (= () empty-deque))
  (is (= (add-first empty-deque 1) [1])))

(deftest meta-test
  (let [m  {:m -1}
        d0 (with-meta empty-deque m)]
    (is (= m (meta d0)))
    (let [d1 (add-first d0 0)]
      (is (= m
             (meta d1)
             (meta (remove-last d1))
             (meta (empty d1)))))))

(run-tests)
