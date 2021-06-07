(ns data.deque-test
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing run-tests]]
               :clj  [clojure.test :refer [deftest is testing run-tests]])
            [data.deque :refer [deque add-first add-last remove-first remove-last peek-first peek-last]]))

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


(deftest comparable-test
  (is (neg? (compare (deque 1 2) (deque 2 3))))
  (is (neg? (compare (deque 1) (deque 2 3))))
  (is (neg? (compare (deque) (deque 1))))

  (is (pos? (compare (deque 2 3) (deque 1 2))))
  (is (pos? (compare (deque 2 3) (deque 1))))
  (is (pos? (compare (deque 1) (deque))))

  (is (zero? (compare (deque) (deque))))
  (is (zero? (compare (deque 1) (deque 1)))))

(deftest conj-test
  (let []
    (is (= (conj #{} (deque 1)) #{(deque 1)}))
    (is (= (conj #{} (deque 1) (deque 1)) #{(deque 1)}))
    (is (= (conj #{} (deque 1) (deque 2)) #{(deque 1) (deque 2)})))

  (let [ss (conj (sorted-set) (deque 1))]
    (is (= (count ss) 1))
    (is (= (deque 1) (first ss))))

  (let [dqs (->> (map deque (shuffle (range 10)))
                 (into (sorted-set)))]
    (is (= (map deque (range 10)) (seq dqs)))))
