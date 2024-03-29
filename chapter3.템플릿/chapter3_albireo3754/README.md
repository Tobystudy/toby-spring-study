## 3. 템플릿
## 3. 템플릿
- 1, 2장을 거치면서 초난감 DAO 코드에 DI를 적용하였고, 확장과 변경에 용이한 설계구조로 코드를 개선하는 작업을 했다.
- OCP는 확장엔 열려있고 변경엔 닫혀있는 객체지향 패턴이고 이번 챕터에선 그 응용으로 템플릿 패턴, 성질이 다른 코드 중에서 변경이 거의 일어나지 않으며 일정한 패턴으로 유지되는 특성을 가진 부분을 자유롭게 변경되는 성질을 가진 부분으로 독립시켜서 활용할 계획이다.
### 3.1 다시 보는 초난감 DAO
- 개선이 많이된 UserDao 여전히 문제점이 존재한다.
    - 예외상황 처리
    - 예를 들어 DB 커넥션이라는 제한적인 리소스를 공유해 사용하는 JDBC 코드는 에러가 발생할 때 리소스를 반드시 반환하도록 만들어야 한다.
    - C++이나 러스트에선 https://blog.naver.com/kmc7468/220989121076 RAII 패턴으로 해결함
- 먼저 jdbc 삭제 기능의 예외처리 코드를 구현해본다. (커밋 1)
    - 예외 발생 시 connection 반환을 위해 try - catch - finally 구문을 사용한다.
        - 해당 코드는 중첩된 try - catch 문 처리를 해야하기 때문에 가독성의 문제가 있고 에러가 나더라도 connection을 정리하는 과정에서 재귀적인 Exception의 가능성 또한 존재한다.
- 다음으론 JDBC 조회 기능의 예외처리를 구현해본다. (커밋 2)
    - 조회는 삭제보다 더 복잡하다. Connection, PreparedStatement 외에도 ResultSet이 추가되기 때문이다.
- 좀 짜치지만(?) 완벽한 UserDao를 구현하였다.
### 3.2 변하는 것과 변하지 않는 것
- 앞서 말한것 처럼 UserDao는 여전히 아쉽다. 코드가 한숨이 나오는게 try - catch - finally 구문이 2중으로 중첩되어 있기 때문이다.
- 이런 코드를 작성할때 가장 효과적인 방법은 Copy & Paste이다.
    - 요즘은 그것보다 더 좋은 코파일럿이 있다. 커밋 2는 코파일럿이 90% 작성해주었다.
- 하지만 이렇게 속도를 내서 실수를 하면 어떻게 될까? 컴파일 에러가 잡아주는 코드를 삭제했다면 모를까, 커넥션 클로즈 코드를 삭제하면 디비 커넥션 풀 한계 때문에 서버가 죽고 서비스가 중단 될 것이다.
    - ~~이렇게 서비스가 죽더라도 금방 죽였다가 살리면 되지않을까? 주기적으로 서버를 껐다 키는것으로 해결하면 안될까?~~
- 그렇다면 테스트를 통해 예외상황에 리소스를 반납하는지 체크하면 되지 않을까?
    - Connection, PreparedStatement, ResultSet등의 구현 클래스도 필요하고 테스트 하기가 매우 번거롭다. 이걸 개발 하느니 코드를 뒤져보는게 더 빠를 것 같다.
    - 나도 커밋 1을 작성하면서 이 코드를 테스트 할까? 고민 했는데 안하길 잘한것 같다.
- 이런 코드를 효과적으로 다룰 수 있는 방법을 고민하는 것이 개발자이다.
    1. 변하는 성격이 다른 코드를 먼저 찾는다.
    2. 변하는 부분을 변하지 않는 나머지 코드에서 분리한다. -> 메소드 추출 (커밋 3)
    3. 메소드 추출을 했는데 지저분한 부분은 그대로 있고, 특별히 이득이 보이지 않는다. 이럴때 사용할 수 있는 방법은 템플릿 메소드 패턴이다.
    4. 하지만 확장구조가 클래스 설계 시점에 고정되어 있고, 마찬가지로 이득이 보이지 않는다. 이럴땐 OCP 원칙을 잘 지키면서도 템플릿 메소드 패턴보다 더 유연한 전략 패턴을 이용한다. (커밋 4)
    5. 전략 패턴은 필요에 따라 컨텍스트를 유지하고 전략을 바꿔 쓰는건데 커밋 4처럼 작성하면 생성을 UserDao에서 하고 있기 때문에 OCP를 잘 지켰다고 볼 수 없다. 여기서 당연하게도 DI를 사용해서 1장에서 Connection을 분리시키듯 분리시켜 본다. (커밋 5)
### 3.3 JDBC 전략 패턴의 최적화
- 아까 작성했던 전략패턴을 이용해서 add를 개선하고 test를 한번 돌려보자. (커밋 6)
- 여기서 만족을 해선 안된다.
    - 로직마다 상속을 할거야?
    - AddStrategy처럼 변수를 받아야하면 생성자도 만들어야 하는데?
- 이럴땐 로컬 클래스, 익명 클래스를 사용할 수 있을듯
    - 코틀린 owner는 바로 람다를 적용해서 구현해본다. (커밋 7)
    - https://kotlinlang.org/docs/fun-interfaces.html
### 3.4 컨텍스트와 DI
- 전략 패턴의 관점에선 UserDao의 메소드가 클라이언트고, jdbcContextWithStatementStrategy() 메소드는 컨텍스트다. 이걸 외부로 옮겨서 다른 DAO에서도 사용가능하게 만들어보자.
- 그리고 스프링 빈으로 등록해서 DI가 가능하게 만든다. (커밋 8)
- 이번 DI의 특징은 구체 클래스를 스프링 빈으로 등록해서 주입한 것이다.
    - 엄밀한 정의의 DI개념은 아니지만, 스프링은 좀 더 객체의 생성과 관계설정에 대한 제어권한을 오브젝트에서 제거한 IoC의 개념을 포괄하기 때문에 사용을 해도 괜찮다.
    - 또한 UserDao와 JdbcContext가 강한 응집도를 갖고 있긴 하지만, Jdbc 방식이 아닌 다른 방식을 사용하게 되면 결국 UserDao 또한 통째로 바뀌게 될 것이고 테스트에서는 DataSource만 갈아끼면 되기 때문에 굳이 인터페이스를 둘 이유가 없다. (살짝 우덜식)
- 그렇다면 빈으로 등록할 필요는 굳이 없지 않을까?
    - 내부 상태도 없는 이 객체가 수백 개 정도 만들어 진다고 서버에 부담도 안갈것이고 자주 생성 되지 않도록 만들면 GC에 대한 부담도 없어진다.
    - 하지만 JdbcContext의 내부엔 Datasource를 의존성으로 갖고 있기 때문에 빈을 주입하는 곳인 생성자 쪽에서 JdbcContext를 생성하도록 구현할 수도 있을 것 같다.
### 3.5 템플릿과 콜백
- 지금까지 구현했던 패턴을 스프링에서 자주 사용하는 템플릿/콜백 패턴이라고 부른다. 전략 패턴의 컨텍스트를 템플릿이라 부르고, 익명 내부 클래스로 만들어지는 오브젝트를 콜백이라고 부른다.
- 전략 패턴과 달리 탬플릿/콜백은 일반적으로 단일 인터페이스를 이용한다.
    - 실제로 이렇게 만들어야 위에서 사용한 것 처럼 자바에선 Functional Interface, 코틀린에선 SAM 인터페이스를 이용해서 람다형태로 만들기 쉽다. 람다는 좀 더 확장성 있는 구조에 적합하고, 인터페이스는 좀 더 결합되어 있는 형태이긴 함
- 이렇게 만들어 놓은 템플릿/콜백 패턴도 마음에 들지 않는 부분이 있다. 여전히 중복되어 있는 코드가 보인다. 이 코드를 분리해서 JdbcContext 쪽에 박아 넣어보자. (커밋 9)
    - 여기선 선택과 집중의 문제가 있을 수 있지만, 가능하다면 XXXDao쪽이 코드를 많이쓸일이 있기 때문에 JdbcContext에 공통 코드가 많이 있으면 더 좋을 가능성이 높다.
- 여전히 강조하는 내용이지만 이러한 추상화 기술은 스프링의 전유물이 아니다. 스프링은 단지 중간중간에 DI를 잘 해주는 기름칠 역할만 할 뿐이다.

### 3.6 스프링의 JdbcTemplate
- 이제 맛보기 JdbcContext는 버리고 훨씬 강력하고 편리한 기능을 제공해주는 JdbcTemplate으로 바꿔본다. (커밋 10)
- 테스트도 보완해줘야 한다. 예외적인 부분은 테스트하기가 어렵기 때문에, 그리고 개발자들이 하기 싫어하기 때문에 오히려 더욱 해야하는 테스트이다.
    - 하기 싫어하는 일이야 말로 꼭 해야하는 일이라는 말이 있다.