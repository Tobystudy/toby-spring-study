# java 배경

## 자바는 기본적으로 커뮤니티 기반으로 성장

- 파이썬 만큼은 아니지만, Sun에서 자바 언어 개발 이후 커뮤니티로 표준화 권리를 이양한 후 오라클로 인수가 되긴 했지만 여전히 커뮤니티 기반으로 스펙이 정의되고 방향성이 정의됨

## Java Community Process와 Java Spec Requests

JSR은 가장 처음 제기되는 요구 사항을 바탕으로 초기 투표를 통해 논의할 것인지 결정하고, 이어서 커뮤니티 리뷰 일자를 정해 이때까지 만들어진 규격을 커뮤니티에 가입된 사람들을 대상으로 리뷰하게 된다. 그리고 나서 퍼블릭 리뷰에 들어가는데, 이때 피드백을 받아 채택하는 단계를 거친다.



# 빈

## Java Beans


> Sun에서 정의한 특정 클래스 컨벤션
>
>  Contains classes related to developing beans -- components based on the JavaBeansTM architecture.


https://docs.oracle.com/javase/6/docs/api/java/beans/package-summary.html

재사용 가능한 소프트웨어 컴포넌트 모델

- All properties are private (use getters/setters)
- A public no-argument constructor
- Implements Serializable.

- 비주얼 컴포넌트에서 유래하여 jsp 등을 거치면서 프론트 위주에서 사용되는 간단한 구조체형태를 bean으로 칭함
- 실무에서는 잘 쓰지 않는 용어

## EJB Enterprise java beans

- EJB는 서버측 컴포넌트 아키텍처 목적으로 설계되었고, 이때의 Bean은 서버측에서 실행되는 재사용 가능한 비즈니스 로직 컴포넌트
- 세션빈, 엔티티 빈, 메시지 빈등으로 정의되며 빈 컨테이너에 의해 관리됨



## Spring Beans

- 스프링 프레임워크에서 애플리케이션의 핵심을 이루는 객체로 IoC 컨테이너에 의해 생명주기가 관리되는 객체를 총칭


# 관심사 분리와 Compositon over Inheritance

- 관심사 분리를 통해 코드의 응집도를 높이고, 관심사가 다른 코드를 분리하는 설계는 기본중에 기본
- 그럼 어떻게 분리할까?

## Inheritance

- 책처럼 DB와 연결하고, 쿼리를 수행하는 공통된 기능들을 추상화하고 연결하는 물리 디비가 달라질때마다 연결 방식을 정의하는 하위 구현체를 정의할 수 있음
- 다양한 상속구조의 디자인 패턴을 사용해서 구현가능!

### Template Method
- 공통된 행위를 템플릿화 하고, 세부 구현체에게 세부적인 특정 행위들만 정의하게 하는 디자인 패턴

 ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/233102003990560?raw=true)




- 디자인 패턴은 Simon Sinek의 골든 서클 이론처럼 Why가 중요하다. 사실 구조는 거기서 거기

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/233102550003744?raw=true)

- Template Method 패턴도 핵심은 기본 알고리즘이 템플릿으로 정의되고, 일부단계 혹은 추가적인 행위를 서브 클래스에서 구현하는것


#### Vs Factory Method
- 객체 생성의 스펙을 상위 클래스에서 정의하고 하위 factory가 구체적인 객체 생성방식을 가지는것
- 공통된 생성 알고리즘을 갖는다면 상위클래스에 정의할 수 있으며, 이 경우 해당 구조는 팩토리 메서드 패턴이면서 템플릿 메서드 패턴인셈



### 상속의 문제
#### 강결합 문제
- 자식 클래스는 부모클래스의 모든행위를 알게되고 사용하게 되므로 구현하는 순간 강결합된다.
- 부모클래스의 변경이 자식 모두에게 영향을 줄정도의 강결합이 발생
- 불필요한 기능, 불필요한 속성도 알게되는 문제

### 합성
- 의존을 통해 해결하기
- is - a 관계가 아니라면, 의존으로 풀어내는것이 장기적으로 좋다.
- 참조 관계로 설계를 하면 필요한 만큼, 내가 시그니처를 이용하는 만큼만 결합되므로 그 결합도가 낮다.


### 합성은 결국 참조를 갖는다. 참조 그래프


![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/233107438993440?raw=true)


- 객체가 참조를 갖기 위해서는 참조 정보를 얻어야함. 어디서?

#### 1. 객체 생성시 참조하는 객체를 생성하면된다

- 객체 그래프상 최상위부터 만들어가면서 DFS로 객체를 만들어가면 됨. 그런식으로 코드를 작성하면 된다

**문제점**

- 객체가 참조하는 객체를 생성한다는건, 생성의 책임이 생긴다는 것
- 또한 상위 인터페이스를 바라보더라도 구체적인 구현체를 생성하는 순간 이미 인터페이스를 쓰는 이점을 잃어버림


#### 2. 참조 의존성을 외부에서 주입해주기(Depency Injection)

객체 그래프상 가장 말단부터 생성하고 해당 객체를 사용하는 객체를 만들때 주입시켜주는 방식 or Setter 로 주입시켜주기



- 그럼 최말단 노드 객체들은 누가 만들지? 객체그래프는 누가 그리고 누가 조립하지?

==> 클라이언트

# framework가 이 객체들을 생성해주면 안되나? : 제어의 역전(IOC)

## framework vs library

- 라이브러리 규모가 크면 프레임워크, 작으면 라이브러리? 아니다
- 핵심은 코드 제어권을 누가 갖는가
- 내가 코드를 제공해서 전체 코드가 동작한다면 프레임워크이며, 내가 사용하는 코드에 해당 코드를 추가한다면 라이브러리
- 제어의 역전(Inversion of Control)


## Dependency Injection by Inversion of Control Framework : Spring

참고 : https://github.com/jinia91/tobyspring-sample-project/pull/3/files#diff-376daf89f64e510e6e15d918a2c6d20511041a8b8f2dd9faa085da624871a756

- 제어의 역전으로 의존성을 주입해주는 역할을 해주는것이 바로 Spring Context 

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/233111914598432?raw=true)

- 스프링의 핵심은 DI를 Ioc로 해주는 팩토리

# Singleton Pattern & Singleton Object

- Singleton: 객체가 하나가 있음을 보장
- Singleton Pattern : 객체를 하나만 만들도록 코드를 작성하는 패턴
- Singleton Object : 단 하나 뿐인 객체

## Singleton pattern is anti-pattern

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/233113999245344?raw=true)

### Cons

- static으로 객체 생성방식이 정의되고, 객체를 가져오는 로직도 존재하므로 SRP를 위반하는 셈
- 전역 접근이 가능해져 유지보수에 결코 바람직하지 않다
- 동시성 이슈를 핸들링하기 쉽지않음


### 내가 쓸 객체를 싱글턴을 보장하게 미리 등록하고 쓰면 안될까? : Singleton Registry

- 스프링 컨테이너가 이 역할을 해준다
- 팩토리로서 만들어낸 객체(bean)들을 중앙화된 context에 단일로 등록하고, 필요히 해당 context에서만 가져오게 함
