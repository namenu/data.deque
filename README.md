# data.deque

[![Clojars Project](https://img.shields.io/clojars/v/data.deque.svg)](https://clojars.org/data.deque)

`data.deque` is a persistent deque for Clojure(Script).
Deque(double-ended queue) is an abstract data type that generalizes a queue, for which elements can be added to or removed from either the front or back.

The implementation of `data.deque` is based on a slightly modified version of [Finger tree](http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf).
It gives O(1) access to both ends and amortized O(1) for immutable insertion/deletion.


## Example

```clj
(require '[data.deque :as dq])

;; create an empty deque with `(deque)` or
(def dl (dq/deque 5 4 3 2 1))

(dq/peek-first dl)
;=> 1

(dq/peek-last dl)
;=> 5

(-> dl
    (dq/add-first 0)
    (dq/add-last 6)
    seq)
;=> (0 1 2 3 4 5 6)

(-> dl
    (dq/remove-first)
    (dq/remove-last)
    seq)
;=> (2 3 4)
```


## Differences from [core.data.finger-tree](https://github.com/clojure/data.finger-tree) 
 - ClojureScript support
 - Better performance
 - No unnecessary features for deque
   - Trees are not concatable / splittable
   - No measuring interfaces
   

## Benchmark

| implementation              |    small |   medium |  large |  rate |
| --------------------------- | -------: | -------: | -----: | ----: |
| java.util.ArrayDeque (base) | 37.94ms  | 271.44ms | 3.47s  | x1    |
| clojure.data.finger-tree    | 196.50ms | 1.23s    | 12.88s | x3.86 |
| data.deque (JVM)            | 158.50ms | 595.49ms | 6.13s  | x1.89 |
| data.deque (Browser)        | 152ms    | 807ms    | 7.47s  | x2.31 |


## Why Finger Tree?

[Banker's Deque](https://www.cs.cmu.edu/~rwh/theses/okasaki.pdf) is also a purely functional data structure that guarantee amortized constant time but performs worse due to reverse operation. 
[Real-Time Deque](http://www.math.tau.ac.il/~haimk/adv-ds-2000/jacm-final.pdf) eliminates amortization by "Lazy Rebuilding" technique, but it also has some overhead due to its laziness.
Finger Tree provides a balanced framework for building deque in terms of both time and space complexity.


## Reference
 - [Ralf Hinze and Ross Paterson. Finger trees: a simple general-purpose data structure](http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf)
 - [Chris Okasaki. Purely Functional Data Structures](https://www.cs.cmu.edu/~rwh/theses/okasaki.pdf)
 - [Haim Kaplan and Robert E. Tarjan. Purely Functional, Real-Time Deques with Catenation](http://www.math.tau.ac.il/~haimk/adv-ds-2000/jacm-final.pdf)
