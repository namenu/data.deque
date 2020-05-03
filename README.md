## Persistent Deque (Double Ended Queue)

ClojureScript에서 사용 가능한 finger-tree 기반의 persistent deque를 구현

왜 finger-tree인가?
 - Catenable deque (Kaplan & Tarjan, 1995; Okasaki, 1997)는 효과적인 삽입, 삭제를 보장하지 않는다.
 - Banker's deque는 stack은 빠른 반면 queue는 느린 성능을 보인다.  

core.data.finger-tree와 다른 점?
 - 20%+ faster than finger-tree/double-list
 - ClojureScript 지원
   - 뿐만 아니라 js용으로 만들어진 persistent deque 자료구조도 없음.
 - measure 관련 기능 제거
   - finger-tree를 deque 이외에 다른 용도로 활용하기 위한 자료구조를 제거
 - concatatenation/split 지원하지 않음
   - deque에는 불필요한 인터페이스

성능 개선 idea:
 - Digit, Node에 JS array 사용
 - supply [collection hashes](https://clojure.org/reference/data_structures#_clojure_collection_hashes) 
 - peek 하는 경우 tree rotation이 일어나지 않도록 lazy하게 구현?
 - transient

기타 TODO:
 - banker's deque와 성능 비교 
 - 기타 프로토콜 구현 (PersistentVector/PersistentQueue와 구색 맞추기)
   - ICloneable, IEquiv, ICounted, IIterable, IMeta
 - :bundle 타겟으로 빌드

Reference
 - [Finger trees: a simple general-purpose data structure](http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf)
 - [Purely Functional, Real-Time Deques with Catenation](http://www.math.tau.ac.il/~haimk/adv-ds-2000/jacm-final.pdf)
