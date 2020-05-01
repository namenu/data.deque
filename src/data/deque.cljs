(ns data.deque
  (:require [data.fingertree :as ft :refer [<| |> viewl viewr]]))

(defprotocol IDeque
  (add-first [coll e])
  (remove-first [coll])
  (peek-first [coll])
  (add-last [coll e])
  (remove-last [coll])
  (peek-last [coll]))

(deftype PersistentDeque [tree]
  IDeque
  (add-first [_ v] (PersistentDeque. (<| tree v)))
  (add-last [_ v] (PersistentDeque. (|> tree v)))

  (peek-first [_] (first (viewl tree)))
  (peek-last [_] (first (viewr tree)))

  (remove-first [_] (PersistentDeque. (second (viewl tree))))
  (remove-last [_] (PersistentDeque. (second (viewr tree))))

  ICollection
  (-conj [this v] (add-last this v))

  IStack
  (-peek [this] (peek-last this))
  (-pop [this] (remove-last this))

  IPrintWithWriter
  (-pr-writer [coll writer opts]
    (pr-sequential-writer writer pr-writer "(" " " ")" opts coll))

  ISequential
  ISeqable
  (-seq [this] this)

  ISeq
  (-first [this] (peek-first this))
  (-rest [_]
    (let [[v rest] (viewl tree)]
      (if v
        (PersistentDeque. rest)
        '()))))

(def empty-deque (PersistentDeque. (ft/Empty.)))

(comment
  (let [layer3 (ft/Empty.)
        layer2 (ft/Deep. [[\i \s] [\i \s]] layer3 [[\n \o \t] [\a \t]])
        layer1 (ft/Deep. [\t \h] layer2 [\r \e \e])]
    ;(js/console.log layer1)

    (let [s  (-> (PersistentDeque. layer1)
                 )

          t2 (-> (ft/Empty.)
                 (<| 1))]
      (-> layer1
          ;viewl second
          ;viewl second
          ;viewl second
          ;viewl second
          viewr second
          viewr second
          viewr second
          ))
    ))
