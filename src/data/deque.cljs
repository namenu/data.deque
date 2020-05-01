(ns data.deque
  (:refer-clojure :exclude [Single ->Single Empty ->Empty]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

;; finger-tree
;;
;; Digit := One a | Two a a | Three a a a | Four a a a a
;; Node := Node2 a a | Node3 a a a
;; FingerTree := Empty
;;             | Single a
;;             | Deep (Digit a) (FingerTree (Node a)) (Digit a)

(defprotocol IFingerTree
  ;; <|, |> : FingerTree a -> a -> FingerTree a
  (<| [coll e])
  (|> [coll e])
  ;; viewl, viewr : FingerTree a -> View a (FingerTree a)
  (viewl [coll])
  (viewr [coll]))

;; somehow forward declaration for deftype doesn't work and finding the reason behind is hard
;; because I couldn't find a safe way to unload/reload class definitions.
;; so make some additional constructors instead and use is as forward decls.
(declare single-tree deep)

(deftype Empty []
  IFingerTree
  (<| [_ e] (single-tree e))
  (|> [_ e] (single-tree e))
  (viewl [_])
  (viewr [_]))

(deftype Single [b]
  IFingerTree
  (<| [_ a] (deep [a] (Empty.) [b]))
  (|> [_ a] (deep [b] (Empty.) [a]))
  (viewl [_] [b (Empty.)])
  (viewr [_] [b (Empty.)])
  )

(defn single-tree [x]
  (Single. x))

(defn to-tree [[a b c d]]
  (cond
    (nil? b) (single-tree a)
    (nil? c) (deep [a] (Empty.) [b])
    (nil? d) (deep [a b] (Empty.) [c])
    :else (deep [a b] (Empty.) [c d])))

(defn deepl [pr m sf]
  (if (seq pr)
    (if-let [[a m'] (viewl m)]
      (deep a m' sf)
      (to-tree sf))
    (deep pr m sf)))

(defn deepr [pr m sf]
  (if (seq sf)
    (if-let [[m' a] (viewr m)]
      (deep pr m' a)
      (to-tree pr))
    (deep pr m sf)))

(deftype Deep [pr m sf]
  IFingerTree
  (<| [_ x]
    (let [pr' (into [x] pr)]
      (if (<= (count pr') 4)
        (Deep. pr' m sf)
        (Deep. (subvec pr' 0 2)
               (<| m (subvec pr' 2))
               sf))))

  (|> [_ x]
    (let [sf' (conj sf x)]
      (if (<= (count sf') 4)
        (Deep. pr m sf')
        (Deep. pr
               (|> m (subvec sf' 0 3))
               (subvec sf' 3)))))

  (viewl [_]
    [(first pr) (deepl (vec (rest pr)) m sf)])

  (viewr [_]
    [(last sf) (deepr pr m (vec (butlast sf)))])
  )

(defn deep [pr m sf]
  (Deep. pr m sf))


;;

(deftype PersistentDeque [tree]
  IDeque
  (add-first [_ e]
    (<| tree e))
  (add-last [_ e]
    (|> tree e))
  )

(def empty-deque (PersistentDeque. (Empty.)))

(comment
  (-> (empty-deque)
      (add-first 10)))
