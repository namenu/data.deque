PersistentDeque

finger-tree 기반의 persistent deque를 구현한다.

왜 finger-tree인가?
 - Chris Okasaki가 소개한 banker's deque와 비교하여 일관되게 빠른 성능을 보여준다. 

core.data.finger-tree와 다른 점?
 - ClojureScript 지원
   - 뿐만 아니라 js용으로 만들어진 persistent deque 자료구조도 없음.
 - measure 관련 기능 제거
   - finger-tree를 deque 이외에 다른 용도로 활용하기 위한 자료구조를 제거
 - concatatenation 지원하지 않음
   - deque에 불필요한 인터페이스

성능 개선 idea:
 - transient 지원
 - peek 하는 경우 tree rotation이 일어나지 않도록 lazy하게 구현?

기타 TODO:
 - banker's deque와 성능 비교 
 - 기타 프로토콜 구현
   - PersistentVector/PersistentQueue와 구색 맞추기, es6-iterator 지원 등
 - :bundle 타겟으로 빌드
 - Clojure 지원

Reference
- http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf
