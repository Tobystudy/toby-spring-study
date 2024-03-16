# 테스트는 왜 해야할까?

## 장기 생산성과 단기 생산성
**단기생산성 측면**
- 빠른 피드백과 대상 범위의 최소화를 통해 단기생산성 증진

**장기 생산성 측면**
- 회귀적 검증을 통한 변경 안정성 확보 / 리팩토링 내성 확보

# 테스트는 어떻게 해야할까?

## 단위 테스트 vs 통합테스트

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/235981568241696?raw=true)

- [practical test pyramid - martin fowler blog](https://martinfowler.com/articles/practical-test-pyramid.html)
- [test pyramid - martin fowler](https://martinfowler.com/bliki/TestPyramid.html)

- 자동화된 단위테스트가 기본

# TDD

- 단기 생산성 목적으로 빠른 피드백 루틴을 위한 Test-First 방법론

## 방법
- 내가 빠르게 결과를 확인하고싶은 테스트를 먼저 작성(Red)
- 구현
- 확인(Green)
- 리팩토링
- 확인
- 리팩토링
- 반복

## is Tdd dead?

- [is Tdd Dead? - dhh](https://dhh.dk/2014/tdd-is-dead-long-live-testing.html)


- 너무 작은 단위의 테스트 작성 유도는 자연스럽게 Mock사용을 강제한다
- Mock사용은 리팩토링 내성을 없앤다
- 장기생산성관점에서 쓸모없는 테스트가 된다


# Di 와 Test

## 전제

- DI는 유연한 설계와 변경 관점에서 항상 옳다
- DI는 테스트 관점에서 단위를 최소화하는데 편하다

## DI 방법
### JUnit-Spring 도움받기

- @ContextConfiguration
- @SpringBootTest
- @DataJpaTest
등등 다양한 컨텍스트 생성방식과 DI 범위 제어기능이 있다.

이중 현재 테스트 케이스에 적절한 범주 선택하기

- 또한 여러 서로 다른 컨텍스트가 필요한 상황일시 @DirtyContext를 적절히 활용하자

### 컨테이너 없이 테스트하기

- 순수하게 직접 의존성을 조립하거나 의존성이 없는 객체를 테스트 대상으로 삼기
- 스프링컨텍스트 자체는 제법 무겁기때문에 왠만하면 이방식으로 작성을 권장

# 학습테스트

- 라이브러리나 프레임워크 학습 목적으로 테스트 코드를 작성하는 것

## 장점

- 다양한 조건에 따른 기능 손쉽게 확인 가능
- 개발중 학습 테스트 참조 가능, 또하나의 레퍼런스이자 문서
- 프레임워크나 라이브러리 버전 업그레이드 시 호환성 검증
- 테스트 작성에 좋은 훈련

# 버그 테스트

- 버그 발생시 수정함에 있어서 해당 버그를 유발하는 테스트를 선 작성 하고 버그를 수정하는 방식

- 회귀검증에 있어서 같은 버그가 재발됨을 막을수 있어서 아주 좋은 테스트 케이스가 된다

- 버그 수정시 반드시 작성하는 습관을 들이자!


