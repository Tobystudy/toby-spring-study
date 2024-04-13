# 스프링의 트랜잭션 추상화

트랜잭션의 이슈는 크게 2가지

1. 트랜잭션의 정의와 선언은 비지니스적인 영역이지만, 동작 자체는 기술적인 영역이므로 레이어 분리가 제대로 되어야한다
2. 선언된 트랜잭션은 스코프 내에서 모든 커넥션을 공유해야하므로 커넥션 제어가 필요하다

## 스프링의 해결방법
### 트랜잭션 동기화 매니저
- jdbc 템플릿과 같은 저수준 기술 레이어에서 커넥션을 공유하고 동기화시키는 매니저 구현체를 두고 이를 추상화하여 비즈니스 로직에서 제어하게끔 구현

``` kotlin
    override fun upgradeUserLevels(): UpgradeUserLevelsInfo {
        val policy = userLevelUpgradePolicy.find {
            EVENT_STATUS == it.supportingEventStatus
        } ?: throw IllegalArgumentException("No policy found for $EVENT_STATUS")

        val targetUsers = userRepository.findAll()
            .sortedBy { it.id }
            .filter { policy.canUpgradeLevel(it) }

        TransactionSynchronizationManager.initSynchronization()
        DataSourceUtils.getConnection(dataSource).use { connection ->
            connection.autoCommit = false
            try {
                targetUsers.forEach { upgradeUserLevel(it, policy) }
            } catch (e: Exception) {
                throw e
            } finally {
                TransactionSynchronizationManager.unbindResource(dataSource)
                TransactionSynchronizationManager.clearSynchronization()
            }

        }
        return buildInfo(targetUsers)
    }

```

#### 문제점
- 위의 코드는 템플릿 콜백 패턴으로 추출할만한 끔직한 코드
- TransactionSynchronizationManager, DataSourceUtils, connection등 지나치게 기술관련 코드들이 비즈니스 로직과 섞인다
- 트랜잭션 자체도 다양하게 구현되어있어 보다 추상화된 인터페이스가 필요하다


### JTA
- 스프링의 분산트랜잭션 제어 인터페이스
- JTA로 제어하기위해서는 XA(eXtended Architecture)를 지원하는 DBMS에 한한다
- 몽고db, neo4j 등 nosql 대부분은 지원하지 않아 애플리케이션단에서 직접 트랜잭션을 구현해야함


### 최상위 트랜잭션 매니저 : 플랫폼 트랜잭션 매니저


```kotlin
 override fun upgradeUserLevels(): UpgradeUserLevelsInfo {
        val policy = userLevelUpgradePolicy.find {
            EVENT_STATUS == it.supportingEventStatus
        } ?: throw IllegalArgumentException("No policy found for $EVENT_STATUS")

        val targetUsers = userRepository.findAll()
            .sortedBy { it.id }
            .filter { policy.canUpgradeLevel(it) }

        val status = transactionManager.getTransaction(DefaultTransactionDefinition())
        try {
            targetUsers.forEach { upgradeUserLevel(it, policy) }
            transactionManager.commit(status)
        } catch (e: Exception) {
            transactionManager.rollback(status)
            throw e
        }
        return buildInfo(targetUsers)
    }
```

보다 추상화된 인터페이스로 제어가 가능하다

### 템플릿 콜백 / AOP
- 스프링에서는 AOP를 사용해 어노테이션 기반으로 훨씬더 쉽게 트랜잭션을 지원해준다

`@Transactional`

- 사실상 멀티 데이터소스의 JTA 구현이나 다중 트랜잭션 제어등을 할때가 아니고선 쓸일이 없다.


# 단일 책임원칙 SRP
- 트랜잭션제어와 비즈니스 로직은 다른 책임
- TransactionSynchronizationManager을 사용시 커넥션의 제어등 여전히 유저서비스가 트랜잭션 제어와 커넥션 제어의 책임을 가지고 있었지만, 해당 책임을 플랫폼 트랜잭션 매니저 로 넘겼다.
- 또한 플랫폼 트랜잭션 매니저는 트랜잭션 기술자체를 추상화함으로써 기술이 변경시에도 유저서비스에 영향을 주지 않아 OCP를 준수한다

# 테스트 대역

## Definition; Double?, Mock?
![image](https://github.com/Tobystudy/toby-spring-study/assets/85499582/6e4b0c71-536d-4d2f-8583-40fb060dbcd2)


## 테스트 대역(Double)

> 테스트 대역은 모든 유형의 비운영용 가짜 의존성을 설명하는 포괄적인 용어다. 이 용어는 영화 산업의 ’스턴트 대역’이라는 개념에서 비롯됐다. 테스트 대역의 주 용도는 테스트를 편리하게 하는 것이다.
> 단위 테스트 (블라디미르 코리코프, 2021) 5.1



- 테스트 대상(System Under Test, SUT)와 협력자 사이에 일어나는 상호작용을 검사할때 사용되는 모든 가짜객체, 대역의 총칭

- xUnit Test Pattern 의 저자 제라드 메자로스(Gerard Meszaros)가 정의했다고 함

- 실무에서 흔히 말하는 목, 목객체, 목서버가 학술적으론 Test Double 이라고 부름

## 많이 헤깔리고 사람들이 하는말이 다른편

 - 마틴파울러의 아티클  https://martinfowler.com/bliki/TestDouble.html, https://martinfowler.com/articles/mocksArentStubs.html 에서 말하는 Mock의 정의는 행위 검증이 가능하고, 행위검증을 위한 대역을 칭한다.

- xUnit Patterns의 정의에서는 http://xunitpatterns.com/Test%20Double.html 관측을 목적으로 행위 검증이 가능한 대역을 칭함

## 실무적으론… 정리

굳이 이 모든 구분을 다 할 필요가 없다.

실무적으론 명세의 노출 관점에서 

- Mock : Mockk등의 도구로 만들어진 SUT의 협력자. 행위검증, 출력검증이 모두 가능하며 특정 값을 목킹할 수 있음

- Stub : 인터페이스를 기반으로 직접 구현한 가짜 객체

위처럼 마틴파울러의 개념으로 구분하거나

- Mock : Mockk등의 도구로 만들어진 SUT의 협력자중 행위 검증만을 위한 객체

- Stub : Mockk등의 도구로 만들어진 SUT의 협력자중 출력이 존재하는 객체

- fake : 인터페이스를 기반으로 직접 구현한 가짜 객체 / 거의 동일한 동작을 보장하는 가짜 구현체

위처럼 구분해도된다.
