(ns data.deque
  (:require [data.fingertree :as ft :refer [<| |> viewl viewr peekl peekr empty-tree]]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(deftype PersistentDeque [^not-native tree]
  IDeque
  (add-first [_ v] (PersistentDeque. (<| tree v)))
  (add-last [_ v] (PersistentDeque. (|> tree v)))

  (peek-first [_] (peekl tree))
  (peek-last [_] (peekr tree))

  (remove-first [_] (PersistentDeque. (second (viewl tree))))
  (remove-last [_] (PersistentDeque. (second (viewr tree))))

  IStack
  (-peek [this] (peek-last this))
  (-pop [this] (remove-last this))

  ICollection
  (-conj [this v] (add-last this v))

  IEmptyableCollection
  (-empty [_] (.-EMPTY PersistentDeque))

  ISequential
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

(set! (.-EMPTY PersistentDeque) (PersistentDeque. empty-tree))
(def empty-deque (.-EMPTY PersistentDeque))

(defn deque [& coll]
  (PersistentDeque. (reduce |> empty-tree coll)))
