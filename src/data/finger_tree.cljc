(ns data.finger-tree
  (:refer-clojure :exclude [Single ->Single Empty ->Empty]))

;(set! *warn-on-reflection* true)

;; finger-tree
;;
;; Digit := One a | Two a a | Three a a a | Four a a a a
;; Node := Node2 a a | Node3 a a a
;; FingerTree := Empty
;;             | Single a
;;             | Deep (Digit a) (FingerTree (Node a)) (Digit a)

#?(:cljs
   (defn digit
     ([a] (array a))
     ([a b] (array a b))
     ([a b c] (array a b c))
     ([a b c d] (array a b c d)))

   :clj
   (defn ^:static digit [& args]
     (object-array args)))

#?(:cljs
   (defn node
     ([a b] (array a b))
     ([a b c] (array a b c)))

   :clj
   (defn ^:static node [& args]
     (object-array args)))

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

(defn deepl [^objects pr m sf]
  (if (zero? (alength pr))
    (let [[a m'] (viewl m)]
      (if a
        (->Deep a m' sf)
        (to-tree sf)))
    (->Deep pr m sf)))

(defn deepr [pr m ^objects sf]
  (if (zero? (alength sf))
    (let [[a m'] (viewr m)]
      (if a
        (->Deep pr m' a)
        (to-tree pr)))
    (->Deep pr m sf)))

(defn cut-first [^objects pr]
  #?(:cljs (.slice pr 1)
     :clj  (java.util.Arrays/copyOfRange pr 1 (alength pr))))

(defn cut-last [^objects sf]
  #?(:cljs (.slice sf 0 -1)
     :clj  (java.util.Arrays/copyOf sf (- (alength sf) 1))))

(deftype Deep [^objects pr m ^objects sf]
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

  (viewl [this]
    [(peekl this) (deepl (cut-first pr) m sf)])

  (viewr [this]
    [(peekr this) (deepr pr m (cut-last sf))])

  (peekl [_]
    (aget pr 0))

  (peekr [_]
    (aget sf (- (alength sf) 1))))


#?(:cljs
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
       (-write writer ")"))))


(comment
  (let [layer3 (ft/->Empty)
        layer2 (ft/->Deep (ft/digit (ft/node \i \s) (ft/node \i \s))
                          layer3
                          (ft/digit (ft/node \n \o \t) (ft/node \a \t)))
        layer1 (ft/->Deep (ft/digit \t \h)
                          layer2
                          (ft/digit \r \e \e))]
    ))
