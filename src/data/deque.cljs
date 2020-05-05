(ns data.deque
  (:require [data.fingertree :as ft :refer [<| |> viewl viewr peekl peekr empty-tree]]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(deftype ^:private PersistentDeque [meta count tree ^:mutable __hash]
  IDeque
  (add-first [_ v] (PersistentDeque. meta (inc count) (<| tree v) __hash))
  (add-last [_ v] (PersistentDeque. meta (inc count) (|> tree v) __hash))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_] (PersistentDeque. meta (max (dec count) 0) (second (viewl tree)) __hash))
  (remove-last [_] (PersistentDeque. meta (max (dec count) 0) (second (viewr tree)) __hash))

  ICloneable
  (-clone [_] (PersistentDeque. meta count tree __hash))

  IWithMeta
  (-with-meta [coll new-meta]
    (if (identical? new-meta meta)
      coll
      (PersistentDeque. new-meta count tree __hash)))

  IMeta
  (-meta [_] meta)

  ISeq
  (-first [this] (peek-first this))
  (-rest [this] (remove-first this))

  IStack
  (-peek [this] (peek-last this))
  (-pop [this] (remove-last this))

  ICollection
  (-conj [this v] (add-last this v))

  IEmptyableCollection
  (-empty [_] (.-EMPTY PersistentDeque))

  ISequential
  IEquiv
  (-equiv [coll other] (equiv-sequential coll other))

  IHash
  (-hash [coll] (caching-hash coll hash-ordered-coll __hash))

  ISeqable
  (-seq [this]
    (if (instance? ft/Empty tree)
      nil
      this))

  ICounted
  (-count [_] count)

  IPrintWithWriter
  (-pr-writer [coll writer opts]
    (pr-sequential-writer writer pr-writer "(" " " ")" opts coll)))

(set! (.-EMPTY PersistentDeque) (PersistentDeque. nil 0 empty-tree nil))

(def empty-deque (.-EMPTY PersistentDeque))

(defn deque [& coll]
  (into empty-deque coll))
