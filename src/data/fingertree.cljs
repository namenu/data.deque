(ns data.fingertree
  (:refer-clojure :exclude [Single ->Single Empty ->Empty]))

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
(declare empty-tree single-tree deep)

(deftype Empty []
  IFingerTree
  (<| [_ e] (single-tree e))
  (|> [_ e] (single-tree e))
  (viewl [_] [nil empty-tree])
  (viewr [_] [nil empty-tree]))

(def empty-tree (Empty.))

(deftype Single [v]
  IFingerTree
  (<| [_ a] (deep [a] empty-tree [v]))
  (|> [_ a] (deep [v] empty-tree [a]))
  (viewl [_] [v empty-tree])
  (viewr [_] [v empty-tree]))

(defn single-tree [x]
  (Single. x))

(defn to-tree [[a b c d]]
  (cond
    (nil? b) (single-tree a)
    (nil? c) (deep [a] empty-tree [b])
    (nil? d) (deep [a b] empty-tree [c])
    :else (deep [a b] empty-tree [c d])))

(defn deepl [pr m sf]
  (if (seq pr)
    (deep pr m sf)
    (let [[a m'] (viewl m)]
      (if a
        (deep a m' sf)
        (to-tree sf)))))

(defn deepr [pr m sf]
  (if (seq sf)
    (deep pr m sf)
    (let [[a m'] (viewr m)]
      (if a
        (deep pr m' a)
        (to-tree pr)))))

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
    [(last sf) (deepr pr m (vec (butlast sf)))]))

(defn deep [pr m sf]
  (Deep. pr m sf))


(extend-protocol IPrintWithWriter
  Empty
  (-pr-writer [_ writer _]
    (-write writer "Empty"))

  Single
  (-pr-writer [coll writer opts]
    (-write writer "Single: ")
    (pr-writer (.-v coll) writer opts))

  Deep
  (-pr-writer [coll writer opts]
    (-write writer "(")
    (pr-writer (.-pr coll) writer opts)
    (-write writer " ")
    (pr-writer (.-m coll) writer opts)
    (-write writer " ")
    (pr-writer (.-sf coll) writer opts)
    (-write writer ")")))
