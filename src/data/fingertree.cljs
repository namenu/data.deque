(ns data.fingertree
  (:refer-clojure :exclude [Single ->Single Empty ->Empty]))

;; finger-tree
;;
;; Digit := One a | Two a a | Three a a a | Four a a a a
;; Node := Node2 a a | Node3 a a a
;; FingerTree := Empty
;;             | Single a
;;             | Deep (Digit a) (FingerTree (Node a)) (Digit a)

(defn ^:static-fns digit
  ([a] (array a))
  ([a b] (array a b))
  ([a b c] (array a b c))
  ([a b c d] (array a b c d)))

(defn node
  ([a b] (array a b))
  ([a b c] (array a b c)))

(defprotocol IFingerTree
  ;; <|, |> : FingerTree a -> a -> FingerTree a
  (<| [coll e])
  (|> [coll e])
  ;; viewl, viewr : FingerTree a -> View a (FingerTree a)
  (viewl [coll])
  (viewr [coll])
  ;; peekl, peekr : FingerTree a -> a | nil
  (peekl [coll])
  (peekr [coll]))

(declare empty-tree ->Single ->Deep)

(deftype Empty []
  IFingerTree
  (<| [_ e] (->Single e))
  (|> [_ e] (->Single e))
  (viewl [_] [nil empty-tree])
  (viewr [_] [nil empty-tree])
  (peekl [_] nil)
  (peekr [_] nil))

(def empty-tree (Empty.))

(deftype Single [v]
  IFingerTree
  (<| [_ a] (->Deep (digit a) empty-tree (digit v)))
  (|> [_ a] (->Deep (digit v) empty-tree (digit a)))
  (viewl [_] [v empty-tree])
  (viewr [_] [v empty-tree])
  (peekl [_] v)
  (peekr [_] v))

(defn to-tree [[a b c d]]
  (cond
    (nil? b) (->Single a)
    (nil? c) (->Deep (digit a) empty-tree (digit b))
    (nil? d) (->Deep (digit a b) empty-tree (digit c))
    :else (->Deep (digit a b) empty-tree (digit c d))))

(defn deepl [pr m sf]
  (if (zero? (alength pr))
    (let [[a m'] (viewl m)]
      (if a
        (->Deep a m' sf)
        (to-tree sf)))
    (->Deep pr m sf)))

(defn deepr [pr m sf]
  (if (zero? (alength sf))
    (let [[a m'] (viewr m)]
      (if a
        (->Deep pr m' a)
        (to-tree pr)))
    (->Deep pr m sf)))

(deftype Deep [pr m sf]
  IFingerTree
  (<| [_ x]
    (case (alength pr)
      1 (Deep. (digit x (aget pr 0)) m sf)
      2 (Deep. (digit x (aget pr 0) (aget pr 1)) m sf)
      3 (Deep. (digit x (aget pr 0) (aget pr 1) (aget pr 2)) m sf)
      4 (Deep. (digit x (aget pr 0))
               (<| m (node (aget pr 1) (aget pr 2) (aget pr 3)))
               sf)))

  (|> [_ x]
    (case (alength sf)
      1 (Deep. pr m (digit (aget sf 0) x))
      2 (Deep. pr m (digit (aget sf 0) (aget sf 1) x))
      3 (Deep. pr m (digit (aget sf 0) (aget sf 1) (aget sf 2) x))
      4 (Deep. pr
               (|> m (node (aget sf 0) (aget sf 1) (aget sf 2)))
               (digit (aget sf 3) x))))

  (viewl [_]
    [(first pr) (deepl (.slice pr 1) m sf)])

  (viewr [_]
    [(last sf) (deepr pr m (.slice sf 0 -1))])

  (peekl [_]
    (aget pr 0))

  (peekr [_]
    (aget sf (- (alength sf) 1))))


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


(comment
  (let [layer3 (ft/Empty.)
        layer2 (ft/Deep. [[\i \s] [\i \s]] layer3 [[\n \o \t] [\a \t]])
        layer1 (ft/Deep. [\t \h] layer2 [\r \e \e])]
    ))
