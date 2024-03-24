# 변경될 코드와 변경되지 않을 코드 분리하기 by Design Pattern

- 계산기 만들기를 통해 따라가 보자

## 원본

```kotlin
class CalculatorStep1Duplicated {
    fun sum(path: String): Int {
        var br: BufferedReader? = null
        try {
        br = BufferedReader(FileReader(path))
            var sum = 0
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sum += line!!.toInt()
            }
            br.close()
            return sum
        } catch (e: Exception) {
            println("An error occurred. $e")
            throw e
        } finally {
            try {
            br?.close()
            } catch (e: Exception) {
                println("An error occurred. $e")
                throw e
            }
        }
    }

    fun multiply(path: String): Int {
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(path))
            var sum = 1
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sum *= line!!.toInt()
            }
            br.close()
            return sum
        } catch (e: Exception) {
            println("An error occurred. $e")
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: Exception) {
                println("An error occurred. $e")
                throw e
            }
        }
    }
}
```

- 아주 단순한 덧셈 곱셈 계산기가 있다
- 파일을 읽기 위한 전, 후처리코드와 예외 코드가 중복되고 핵심 알고리즘은 각각 단 한줄 뿐
- 불필요한 중복이 반복되고있다.


- 자바 7 이상이라면 try with resource 패턴을 통해 언어적 차원에서 리소스 전/후처리를 보다 쉽게 해줄수 있지만, 설명을 위해 고전적인 try catch finally를 사용했다.

## 1. 템플릿 메서드
> Defines the skeleton of an algorithm in a method, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithms structure. - GOF -

![image](https://github.com/Tobystudy/toby-spring-study/assets/85499582/a4de737c-4c82-4282-984e-a25f26c05980)

- 부모클래스는 어떤 기능(알고리즘)을 정의하고, 변경되는 부분(templateMethod())은 자식클래스에서 재정의한다.
- 객체지향의 특징인 상속과 다형성을 사용해 공통부분은 부모에 정의하고 개별적인 부분은 구체클래스에 정의하는 방식

### 구현하려면?
- 멱등성 제어부분을 부모 클래스에서 정의하고, 멱등성이 필요한 서비스에서 해당 부모 클래스를 상속받아 집어넣을 로직을 구현하면 된다.

### 문제점은?
- 부모 자식간의 강결합 문제
- 다중 상속이 안되는 문제
- 멱등성을 유지해야할 함수들을 따로 각각 클래스로 만들어서 위의 템플릿 부모를 상속받아야하므로 의도치 않은 분리가 이루어질수도 있음


```kotlin
abstract class CalculatorTemplate {
    abstract val initialValue: Int

    fun calculate(path: String): Int {
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(path))
            var result = initialValue
            var line: String?
            while (br.readLine().also { line = it } != null) {
                result = calculateInternal(result, line!!.toInt())
            }
            br.close()
            return result
        } catch (e: Exception) {
            println("An error occurred. $e")
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: Exception) {
                println("An error occurred. $e")
                throw e
            }
        }
    }
    abstract fun calculateInternal(result: Int, number: Int) : Int

}

// 실제 구체 클래스의 모습
class PlusCalculator(override val initialValue: Int = 0) : CalculatorTemplate() {
    override fun calculateInternal(result: Int, number: Int): Int {
        return result + number
    }
}

class MultiplyCalculator(override val initialValue: Int = 1) : CalculatorTemplate() {
    override fun calculateInternal(result: Int, number: Int): Int {
        return result * number
    }
}

```

- 핵심 : 구체 클래스가 공통 로직을 알 필요가 있을까?

## 2. 전략 패턴
> Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it - GOF -

![image](https://github.com/Tobystudy/toby-spring-study/assets/85499582/3660c31e-f949-46da-9de0-566d0d28f5d3)

- 변경될 부분을 인터페이스로 추출하고 공통 부분이 해당 인터페이스를 보도록 함으로서 공통부분을 가진 클래스와 변경 부분을 가진 클래스간 강결합을 합성과 dip로 풀어낸 형태

``` kotlin
class CalculatorWithStrategy(
    private val strategies: List<CalculatorStrategy>,
) {
    fun sum(path: String): Int {
        return calculate(path, CalculateOperation.SUM)
    }

    fun multiply(path: String): Int {
        return calculate(path, CalculateOperation.MULTIPLY)
    }

    private fun calculate(path: String, operation: CalculateOperation): Int {
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(path))
            val strategy = strategies.find { it.isSupport() == operation }
                ?: throw IllegalArgumentException("not supported operation. operation: $operation")
            var result = strategy.initValue()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                result = strategy.calculate(result, line!!.toInt())
            }
            br.close()
            return result
        } catch (e: Exception) {
            println("An error occurred. $e")
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: Exception) {
                println("An error occurred. $e")
                throw e
            }
        }
    }
}

enum class CalculateOperation {
    SUM, MULTIPLY
}

interface CalculatorStrategy {
    fun calculate(result: Int, number: Int): Int
    fun initValue(): Int
    fun isSupport(): CalculateOperation
}

class PlusStrategy : CalculatorStrategy {
    override fun calculate(result: Int, number: Int): Int {
        return result + number
    }

    override fun initValue(): Int {
        return 0
    }

    override fun isSupport(): CalculateOperation {
        return CalculateOperation.SUM
    }
}

class MultiplyStrategy : CalculatorStrategy {
    override fun calculate(result: Int, number: Int): Int {
        return result * number
    }

    override fun initValue(): Int {
        return 1
    }

    override fun isSupport(): CalculateOperation {
        return CalculateOperation.MULTIPLY
    }
}
```

- 훨씬 결합도가 낮아졌기 때문에 확장에 더욱 열려있고 변경에는 닫혀있게된다.


## 3. 템플릿 / 콜백 패턴

전략패턴이든 템플릿 메서드 패턴이든 클래스가 많아지며 복잡도가 증가하는 문제가 있음.

- 만약 변경 가능 부분의 로직이 재사용성이 많이 떨어지고 가짓수만 많을경우 클래스를 100개 200개 정의해야할지도...
- 물론 자바는 내부 클래스등으로 묶을수 있긴하지만 그래도 복잡도 문제는 여전
- 이를 익명 클래스와 인터페이스만 가지고 콜백 객체 자체를 클라이언트에서 주입함으로서 복잡도를 낮출 수 있음
- 더 나아가 자바 8 이상부터는 함수형 인터페이스와 람다를, 코틀린은 함수 타입 으로 정의함으로서 인터페이스조차 없이 템플릿 / 콜백 패턴이 가능해진다

```kotlin
class CalculatorStep4TemplateCallback {
    fun sum(path: String): Int {
        return doCalculatorCommonTemplate(path, { line, sum -> sum + line.toInt() }, 0)
    }

    fun multiply(path: String): Int {
        return doCalculatorCommonTemplate(path, { line, sum -> sum * line.toInt() }, 1)
    }

    fun <T> doCalculatorCommonTemplate(
        path: String,
        callBack: (String, T) -> T,
        result: T
    ): T {
        try {
            BufferedReader(FileReader(path)).use { reader ->
                var line: String?
                var result: T = result
                while (reader.readLine().also { line = it } != null) {
                    result = callBack(line!!, result)
                }
                return result!!
            }
        } catch (e: Exception) {
            println("An error occurred. $e")
            throw e
        }
    }
}
```

- 위처럼 자주 사용되는 콜백이라면 함수로 정의해둘 수도 있고

```kotlin
    @Test
    fun testSubtract() {
        val calculatorStep1 = CalculatorStep4TemplateCallback()
        val path = "src/test/resources/numbers.txt"
        val result = calculatorStep1.doCalculatorCommonTemplate(path, { line, sum -> sum - line.toInt() }, 0)
        result shouldBe -10
    }
```

위처럼 클라이언트가 콜백 함수를 넘겨 확장에 아주 자유로워질 수 있다.

# 스프링에서 사용되는 템플릿 콜백 패턴들

## JDBC Template
- 1. 순수하게 DB에 연결하기
- 2. JDBC로 연결하기
- 3. 스프링 JDBC로 추상화해 연결하기
- 4. 스프링 JPA로 더 추상화해 연결하기

여기서 2번에 해당하는 기술

## RestTemplate
- 순수하게 소켓프로그래밍으로 tcp 연결해 http 요청하기
- http 클라이언트 구현체 사용하기(http template)
- RestClinet, WebClient 등 더 추상화된 api쓰기
- deprecated됬음

## TransactionTemplate
- 트랜잭션 직접 관리할 필요가 있을때 사용
- 선언적 트랜잭션 관리하기

## MongoTemplate
- mongoClient
- mongoTemplate
- mongoRepository

순서로 추상화 정도

스프링 프레임워크에서 제공되는 다양한 템플릿 콜백 패턴의 궇녀체들은 현재 실무에서는 상당히 로우단 기술이거나 deprecated되었고 더 추상화된 구현체가 많다.

내부 동작을 알기위해 까보는일은 많지만 완벽하게 알필요는 없음
