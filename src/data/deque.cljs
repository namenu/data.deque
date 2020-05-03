(ns data.deque
  (:require [data.fingertree :as ft :refer [<| |> viewl viewr peekl peekr empty-tree]]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(deftype ^:private PersistentDeque [tree ^:mutable __hash]
  IDeque
  (add-first [_ v] (PersistentDeque. (<| tree v) __hash))
  (add-last [_ v] (PersistentDeque. (|> tree v) __hash))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_] (PersistentDeque. (second (viewl tree)) __hash))
  (remove-last [_] (PersistentDeque. (second (viewr tree)) __hash))

  IStack
  (-peek [this] (peek-last this))
  (-pop [this] (remove-last this))

  ICollection
  (-conj [this v] (add-last this v))

  IEmptyableCollection
  (-empty [_] (.-EMPTY PersistentDeque))

  ISequential

  IHash
  (-hash [coll] (caching-hash coll hash-ordered-coll __hash))

  ISeqable
  (-seq [this]
    (if (instance? ft/Empty tree)
      nil
      this))

  ISeq
  (-first [this] (peek-first this))
  (-rest [this] (remove-first this))

  IPrintWithWriter
  (-pr-writer [coll writer opts]
    (pr-sequential-writer writer pr-writer "(" " " ")" opts coll)))

(set! (.-EMPTY PersistentDeque) (PersistentDeque. empty-tree nil))
(def empty-deque (.-EMPTY PersistentDeque))

(defn deque [& coll]
  (PersistentDeque. (reduce |> empty-tree coll) nil))
