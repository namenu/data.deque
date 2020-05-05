(ns data.tests
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [data.deque :refer [PersistentDeque deque add-first add-last remove-first remove-last peek-first peek-last]]
            [data.finger-tree :as ft]))

(def empty-deque (.-EMPTY PersistentDeque))

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

(deftest ft-test
  (let [layer3 (ft/Empty.)
        layer2 (ft/Deep. (ft/digit (ft/node \i \s) (ft/node \i \s))
                         layer3
                         (ft/digit (ft/node \n \o \t) (ft/node \a \t)))
        layer1 (ft/Deep. (ft/digit \t \h)
                         layer2
                         (ft/digit \r \e \e))]
    (is (= (str (.-pr layer1))
           (str (array \t \h))))
    (is (= (str (.-sf layer1))
           (str (array \r \e \e))))))

(run-tests)
