## Persistent Deque

Deque(double-ended queue) is an abstract data type that generalizes a queue,
for which elements can be added to or removed from either the front or back.

`data.deque` is a persistent implementation of deque for Clojure(Script)
which is based on slightly modified version of [finger tree](https://en.wikipedia.org/wiki/Finger_tree).
`data.deque` gives O(1) access to both ends and amortized O(1) for immutable insertion/deletion.   


#### Why Finger Tree?

Bankers Deque is also a purely functional data structure that guarantee amortized constant time but performs worse due to reverse operation. 
Real-Time Deque eliminates amortization by "Lazy Rebuilding" technique, but it also has some overhead due to its laziness.
Finger Tree provides a balanced framework for building deque in terms of both time and space complexity.


#### Differences from [core.data.finger-tree](https://github.com/clojure/data.finger-tree) ? 
 - ClojureScript support
 - Better performance
 - No unnecessary features for deque
   - Trees are not concatable / splittable
   - Measurements only being used for counting


#### Example

```clj
(require '[data.deque :refer [deque]])

(def dl (deque 5 4 3 2 1))

(peek-first dl)
=> 1

(peek-last dl)
=> 5

(-> dl (add-first 0) (add-last 6) seq)
=> (0 1 2 3 4 5 6)

(-> dl (remove-first) (remove-last) seq)
=> (1 2 3)
```


### Benchmark:

|                             | small    | medium   | large  | rate  |
| --------------------------- | -------- | -------- | ------ | ----- |
| java.util.ArrayDeque (base) | 37.94ms  | 271.44ms | 3.47s  | x1    |
| clojure.data.finger-tree    | 196.50ms | 1.23s    | 12.88s | x3.86 |
| data.deque (JVM)            | 158.50ms | 595.49ms | 6.13s  | x1.89 |
| data.deque (Browser)        | 152ms    | 807ms    | 7.47s  | x2.31 |


### Reference
 - [Finger trees: a simple general-purpose data structure](http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf)
 - [Purely Functional, Real-Time Deques with Catenation](http://www.math.tau.ac.il/~haimk/adv-ds-2000/jacm-final.pdf)
