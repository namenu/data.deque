(ns data.deque
  (:require [data.finger-tree :refer [<| |> viewl viewr peekl peekr empty-tree]]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(deftype PersistentDequeSeq [meta tree ^:mutable __hash]
  Object
  (toString [coll]
    (pr-str* coll))
  (equiv [this other]
    (-equiv this other))

  IWithMeta
  (-with-meta [coll new-meta]
    (if (identical? new-meta meta)
      coll
      (PersistentDequeSeq. new-meta tree __hash)))

  IMeta
  (-meta [_] meta)

  ISeq
  (-first [_] (peekl tree))
  (-rest  [coll]
    (let [tree' (second (viewl tree))]
      (if (identical? tree' empty-tree)
        (-empty coll)
        (PersistentDequeSeq. meta tree' nil))))

  INext
  (-next [_]
    (let [tree' (second (viewl tree))]
      (if-not (identical? tree' empty-tree)
        (PersistentDequeSeq. meta tree' nil))))

  ICollection
  (-conj [coll o] (cons o coll))

  IEmptyableCollection
  (-empty [_] (-with-meta (.-EMPTY List) meta))

  ISequential
  IEquiv
  (-equiv [coll other] (equiv-sequential coll other))

  IHash
  (-hash [coll] (caching-hash coll hash-ordered-coll __hash))

  ISeqable
  (-seq [coll] coll)

  IPrintWithWriter
  (-pr-writer [coll writer opts]
    (pr-sequential-writer writer pr-writer "(" " " ")" opts coll)))

(es6-iterable PersistentDequeSeq)

(deftype PersistentDeque [meta count tree ^:mutable __hash]
  IDeque
  (add-first [_ v] (PersistentDeque. meta (inc count) (<| tree v) nil))
  (add-last [_ v] (PersistentDeque. meta (inc count) (|> tree v) nil))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_] (PersistentDeque. meta (max (dec count) 0) (second (viewl tree)) nil))
  (remove-last [_] (PersistentDeque. meta (max (dec count) 0) (second (viewr tree)) nil))

  Object
  (toString [coll]
    (pr-str* coll))
  (equiv [this other]
    (-equiv this other))

  ICloneable
  (-clone [_] (PersistentDeque. meta count tree __hash))

  IWithMeta
  (-with-meta [coll new-meta]
    (if (identical? new-meta meta)
      coll
      (PersistentDeque. new-meta count tree __hash)))

  IMeta
  (-meta [_] meta)

  ;; - Need not to be a ISeq.
  ;; Note cljs.core.PersistentQueue acts as ISeq while clojure.lang.PersistentQueue does not.
  ;; This discrepancy leads me to comment this out.
  ;ISeq
  ;(-first [coll] (peek-first coll))
  ;(-rest [coll] (rest (seq coll)))

  IStack
  (-peek [this] (peek-last this))
  (-pop [this] (remove-last this))

  ICollection
  (-conj [this v] (add-last this v))

  IEmptyableCollection
  (-empty [_] (with-meta (.-EMPTY PersistentDeque) meta))

  ISequential
  IEquiv
  (-equiv [coll other] (equiv-sequential coll other))

  IHash
  (-hash [coll]
    (caching-hash coll hash-ordered-coll __hash))

  ISeqable
  (-seq [_]
    (if-not (identical? empty-tree tree)
      (PersistentDequeSeq. nil tree nil)))

  ICounted
  (-count [_] count)

  IPrintWithWriter
  (-pr-writer [coll writer opts]
    (pr-sequential-writer writer pr-writer "#deque [" " " "]" opts (seq coll))))

(set! (.-EMPTY PersistentDeque) (PersistentDeque. nil 0 empty-tree nil))

(es6-iterable PersistentDeque)

(defn deque [& coll]
  (into (.-EMPTY PersistentDeque) coll))
