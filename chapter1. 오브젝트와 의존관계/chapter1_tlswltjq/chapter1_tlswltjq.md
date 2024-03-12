스프링이 지지하는 가치와 이루고자하는 목표는 무엇인가.
# 1장 오브젝트와 의존관계
스프링 핵심 철학 : 객체지향 프로그래밍이 제공하는 폭넓은 해택을 누릴 수 있게하자.<br>
-> 가장 관심두는것 또한 객체 <br>


## 1.1 초난감 DAO
#### 자바빈 : 원래는 비주얼툴에서 조작 가능한 컴포넌트를 지칭<br>이제 자바빈 이라고 한다면 다음 두 관례를 따라 만들어진 오브젝트를 지칭한다.
1. 디폴트 생성자 : 자바빈은 파라미터가 없는 기본 생성자를 갖고 있어야한다. 툴,프레임워크에서 [리플렉션](https://hudi.blog/java-reflection/)을 이용해 오브젝트를 생성하기때문
2. 프로퍼티 : 자바빈이 노출하는 이름을 가진 소성을 프로퍼티라고 한다. 프로퍼티는 set으로 시작하는 수정자 메서드(setter)와 get으로 시작하는 접근자 메서드(getter)를 이용해 수정, 조회한다.  
### 1.1.1 User
```java
package org.example.chapter1_tlswltjq.domain;

public class User {
    String id;
    String name;
    String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```
```sql
create table users(
    id varchar(10) primary key ,
    name varchar(20) not null ,
    password varchar(10) not null
)
```
### 1.1.2 UserDao
#### JDBC 연결의 일반적인 순서
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;

import java.sql.*;

public class UserDao {
    public void add(User user) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:~/toby", "spring", "book");

        PreparedStatement ps = c.prepareStatement(
                "insert info users(id, name, password) values(?,?,?)"
        );
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:~/toby", "spring", "book");

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );
        
        ps.setString(1, id);
        
        ResultSet rs = ps.executeQuery();
        
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        
        rs.close();
        ps.close();
        c.close();
        
        return user;
    }
}

```
### 1.1.3 Main()을 이용한 DAO 테스트 코드
```java
package org.example.chapter1_tlswltjq;

import org.example.chapter1_tlswltjq.dao.UserDao;
import org.example.chapter1_tlswltjq.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class Chapter1TlswltjqApplication {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        SpringApplication.run(Chapter1TlswltjqApplication.class, args);

        UserDao dao = new UserDao();

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);
        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + " 조회 성공");
    }
}
```
실행결과
```shell
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.3)

2024-03-11T20:33:26.850+09:00  INFO 31269 --- [           main] o.e.c.Chapter1TlswltjqApplication        : Starting Chapter1TlswltjqApplication using Java 17.0.10 with PID 31269 (/Users/sinjiseob/Documents/HTTP Definitive guide study/toby-spring-study/chapter1. 오브젝트와 의존관계/chapter1_tlswltjq/out/production/classes started by sinjiseob in /Users/sinjiseob/Documents/HTTP Definitive guide study/toby-spring-study/chapter1. 오브젝트와 의존관계/chapter1_tlswltjq)
2024-03-11T20:33:26.852+09:00  INFO 31269 --- [           main] o.e.c.Chapter1TlswltjqApplication        : No active profile set, falling back to 1 default profile: "default"
2024-03-11T20:33:27.489+09:00  INFO 31269 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-03-11T20:33:27.494+09:00  INFO 31269 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-03-11T20:33:27.494+09:00  INFO 31269 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.19]
2024-03-11T20:33:27.516+09:00  INFO 31269 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2024-03-11T20:33:27.516+09:00  INFO 31269 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 630 ms
2024-03-11T20:33:27.665+09:00  INFO 31269 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
2024-03-11T20:33:27.670+09:00  INFO 31269 --- [           main] o.e.c.Chapter1TlswltjqApplication        : Started Chapter1TlswltjqApplication in 1.272 seconds (process running for 1.661)
whiteship 등록 성공
백기선
married
whiteship 조회 성공
```
매우 한심한 코드이다.
## 1.2 DAO의 분리
### 1.2.1 관심사의 분리
오브젝트에 대한 설계, 구현코드가 끈임없이 변한다. 개발자는 미래를 바라보고 변화를 대비 해야한다.<br>
미래를 준비하는 중요한 논점은 변화의 폭을 최소화 하는것.<br>
기능변경을 해야할 때 해당 기능만 수정하면서 다른 코드에 영향도 미치지않게 해내는 개발자가 미래의 변화를 잘 대비한것.<br>
이를 이루어 내려면 어떻게 해야할까.

#### 관심사의 분리
관심이 같은것은 객체 안으로, 또는 친한 객체로 모이게하고 관심이 다른것은 가능한 따로 떨어져서 서로 영향을 주지 않도록 분리하는것.

### 1.2.2 커넥션 만들기의 추출
#### UserDao에 존재하는 세 가지 관심사항
1. DB와 연결을 위한 커넥션을 어떻게 가져올까? (어떤 디비를 쓰고 어떤 드라이버와 로그인 정보를 쓰며 커넥션을 생성하는 방법은?)
2. 사용자 등록을 위해 DB에 보낼 SQL문장을 담을 Statement를 만들고 실행하기 (사용자 정보를 Statement에 바인딩하기, SQL을 디비를 통해 실행시키는 방법)
3. 작업이 끊나고 사용한 리소스를 닫아 시스템에 돌려주는것.

#### 중복 코드의 메소드 추출
우선 중복되는 코드를 분리해보자.
```java
private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:~/toby", "sa", "");
        return c;
    }
```
이제 디비의 종류와 접속벙법이 바뀌어 드라이버 클래스와 URL이 바뀌어도 getConnection()메서드만 수정하면 된다!
### 1.2.3 DB 커넥션 만들기의 독립
분리한 메서드가 향후에 또 변경할 가능성이 있다면?<br>
#### 상속을 통한 확장
기존의 UserDao를 한 단계 더 분리하면된다.<br>
구현을 제거하고 getConnection()을 추상메소드로 만들어 add, get()를 호출하는 코드를 그대로 사용할 수 있다.

```java
import java.sql.Connection;
import java.sql.SQLException;

public abstract class UserDao {
    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
//        ...
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        Connection c = getConnection();
//        ...
    }

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;

    public class NUSerDao extends UserDao {
        public Connection getConnection() throws ClassNotFoundException, SQLException {
//            N사 DB connection 생성코드
        }
    }

    public class DUSerDao extends UserDao {
        public Connection getConnection() throws ClassNotFoundException, SQLException {
//            D사 DB connection 생성코드
        }
    }
}
```
UserDao의 상속을통해 클래스레벨로 관심사가 분리. 이제는 상속을 통해 손쉽게 확장될 수 있다.<br>
UserDaosms Connection타입의 인터페이스의 어떤 기능을 사용한다는 데에만 관심이있다.
#### 디자인 패턴
소프트웨어 설계 시 특정 상황에서 자주 만나는 문제를 해결하기 위해 사용할 수 있는 재사용 가능한 솔루션.

#### 템플릿 메소드 패턴
슈퍼클래스에 기본적인 로직의 흐름을 만들고 그 기능의 일부를 추상메소드, 오버라이딩 가능한 protected매소드를 만든 후 서브클래스에서 구현해 사용하는것

#### 팩토리 메소드 패턴
템플릿 메소드 패턴과 마찬가지로 상속을 통해 기능을 확장하는 패턴<br>
[두 패턴의 차이점](https://western-sky.tistory.com/40)

## 1.3 DAO의 확장
### 1.3.1 클래스의 분리
 관심사의 정보를 클래스 단위의 분리 후 UserDao가 이를 직접 이용하게 한다.
```java
package org.example.chapter1_tlswltjq.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionMaker {
    public Connection makeNew() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:~/toby", "sa", "");
        return c;
    }
}
```
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;

import java.sql.*;

public class UserDao {
    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDao() {
        simpleConnectionMaker = new SimpleConnectionMaker();
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = simpleConnectionMaker.makeNew();
        ...
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        Connection c = simpleConnectionMaker.makeNew();
        ...
    }
    ...
}
```
이러한 리팩터링 과정은 검의 기능을 한다고 볼 수도 있다!<br>
SimpleConnectionMaker 하나만을 수정함으로 다양한 상황에서 커넥션을 획득할 수 있게 되었다. <br>

그러나 이제 커넥션을 제공받기 위해서 클래스가 SimpleConnectionMaker라는 구현된 구체클레스임과 커넥션을 얻기위해 사용하는 메서드가 makeNew()임인것을 UserDao는 알고있어야한다.<br>

### 1.3.2 인터페이스의 도입
너무 강하게 결합 되어버린 두 클래스 사이를 느슨한 연결고리로 이어준다.<br>
결과적으로 구체화된 클래스를 통해 오브젝트가 생성되겠지만 인터페이스를 사용한다면 UserDao는 클래스에 대해서 알 필요가 없어져 단지 사용하는 기능이 무엇인지만 알 면 된다.
```java
package org.example.chapter1_tlswltjq.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
    
}
```
```java
package org.example.chapter1_tlswltjq.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        //D사의 Connection을 생성하는 코드
        return null;
    }
}
```
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;

import java.sql.*;

public class UserDao {
    private ConnectionMaker connectionMaker; //구체클래스를 알 필요 엉ㅂㅅ다.

    public UserDao() {
        connectionMaker = new DConnectionMaker(); // 아직은 구체클래스를 호출해야하는 문제가 남아있긴 하다
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();//이제 인터페이스에 정의 된 메소드를 사용하므로 클래스의 변경에 따른 메소드명 변화도 걱정할 필요없다.
        
        ...
        
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        Connection c = connectionMaker.makeConnection();
        
        ...
        
        return user;
    }
}
```
### 1.3.3 관계설정 책임의 분리
#### 관계 설정에 대한 관심
관심사를 분리해냈다고 생각했음에도 불구하고 아직도 UserDao가 구현 클래스를 알아야하는 이유 -> 아직 다른 분리되지않은 관심사가 있기 때문.<br>
바로 UserDao가 어떤 ConnectionMaker의 구현 클래스 오브젝트를 사용할것인가?
클래스 사이에 관계를 만들지 않고 오브젝트 사이에 관계(다이내믹한)를 만들어야한다.
#### 수정된 생성자
```java
public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
```
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;

import java.sql.SQLException;

public class UserTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao dao = new UserDao(connectionMaker);

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);
        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + " 조회 성공");
    }
}

```
이제 UserDao 자신의 관심사(User객체를 바탕으로 데이터베이스에 저장, 조회)이외의 책임은 지지않아 자유롭게 확장 가능하다.

### 1.3.4 원칙과 패턴
#### OCP 개방 폐쇄 원칙
클래스나 모듈의 확장은 열려 있어야 하고, 변경에는 닫혀 있어야 한다.<br>
DB연결 방법에 대한 기능을 확장하는 데는 열려 있어야 하지만, 자신의 핵심 기능을 구현한 코드는 그 변화에 영향을 받지 않아야 한다.

[객체지향 설계의 기초와 원칙 SOLID](https://ko.wikipedia.org/wiki/SOLID_(%EA%B0%9D%EC%B2%B4_%EC%A7%80%ED%96%A5_%EC%84%A4%EA%B3%84))

#### 높은 응집도와 낮은 결합도
응집도가 높다 -> 하나의 모듈/클래스에 하나의 책임/관심사에만 집중되어있다.
결합도가 낮다 -> 책임과 관심사가 다른 오브젝트/모듈가 느슨하게 연결되어 서로 독립적이고 알 필요없이 만들어준다.

#### [전략패턴](https://velog.io/@kyle/%EB%94%94%EC%9E%90%EC%9D%B8-%ED%8C%A8%ED%84%B4-%EC%A0%84%EB%9E%B5%ED%8C%A8%ED%84%B4%EC%9D%B4%EB%9E%80)

## 1.4 제어의 역전(IOC)
### 1.4.1 오브젝트 팩토리
UserDao의 책임을 UserDaoTest가 떠맡게 되었다. 이를 해결해보자

#### 팩토리
객체의 생성 방법을 정하고 생성된 오브젝트를 돌려주는 오브젝트

```java
package org.example.chapter1_tlswltjq.dao;

public class DaoFactory {
    public UserDao userDao(){
        ConnectionMaker connectionMaker = new DConnectionMaker();
        return new UserDao(connectionMaker);
    }
}
```
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;

import java.sql.SQLException;

public class UserTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao dao = new DaoFactory().userDao();
        
        ...
        
    }
}

```
#### 설계도로서의 팩토리
이제 ConnectionMAker의 구현 클래스가 변경이 필요하면 팩토리를 이용해 새로운 소스를 제공하면 되고, 이로서 UserDao는 완전한 독립과 확장을 얻었다.<br>

#### 애플리케이션 "컴포넌트 역할을 하는 오브젝트"와 애플리케이션 "구조를결정하는 오브젝트"를 분리하였다

### 1.4.2 오브젝트 팩토리의 활용
다른 DAO생성 기능이 추가된다면 어떠할까?
```java
package org.example.chapter1_tlswltjq.dao;

public class DaoFactory {
    public UserDao userDao(){
        return new UserDao(new DConnectionMaker());
    }

    public UserDao AccountDao(){
        return new UserDao(new DConnectionMaker());
    }

    public UserDao messageDao(){
        return new UserDao(new DConnectionMaker());
    }
}
```
모든 메소드가 ConnectionMaker의 구현클래스를 생성하게된다.
분리하여 해결해보자
```java
package org.example.chapter1_tlswltjq.dao;

public class DaoFactory {
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    public UserDao AccountDao(){
        return new UserDao(connectionMaker());
    }

    public UserDao messageDao(){
        return new UserDao(connectionMaker());
    }
    
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
```
### 1.4.3 제어권의 이전을 통한 제어관계 역전
#### 일반적인 프로그램의 흐름
1. 프로그램이 시작된다
2. 사용할 오브젝트를 결정
3. 결정한 오브젝트를 생성
4. 생성된 오브젝트에 있는 메소드를 호출
5. 해당 메서드에 필요한 것들을 결정하고 호출
6. -> 2
이 흐름에서 오브젝트는 자신이 사용할 오브젝트를 선택 생성한다.
#### 제어의 역전
제어의 역전에서는 오브젝트가 사용할 오브젝트를 스스로 선택하거나 생성하지 않는다.<br>
프레임워크도 제어의 역전이 적용된 대표적인 기술.<br>

라이브러리와 프레임워크의 차이는 제어의 흐름이 누구의 주도권 아래에 있느냐의 차이

## 1.5 스프링의 IoC
### 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
- <b>빈 /  bean : 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트
- 팩토리 : IoC 오브젝트 빈의 생성과 관계설정 같은 제어를 담당
- 애플리케이션 컨텍스트 : 빈팩토리의 확장</b>

빈 팩토리 라고 한다면 IoC의 기본 기능에 초점을 맞춘 것이고, 애플리케이션 컨텍스트 라고 말할 때는 전반에 걸쳐 모든 구성요소의 제어를 담당하는 IoC엔진으로서의 의미가 부각됨.
```java
package org.example.chapter1_tlswltjq.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration                          //애플리케이션 컨텍스트 혹은 빈팩토리가 사용할 설정정보라는 표시 
public class DaoFactory {
    @Bean                               //오브젝트 생성을 담당하는 Ioc용 메소드 라는 표시
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
```

### 1.5.2 애플리케이션 컨텍스트의 동작방식
어플리케이션 컨텍스트는 ApplicationContext인터페이스를 구현하는데 ApplicationContext는 BeanFactory를 상속하므로 일종의 빈 팩토리이다.
#### 어플리케이션 컨텍스트사용의 장점
- 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다.
- 어플리케이션 컨텍스트는 종합 IoC 서비스를 제공해준다.<br>: 오브젝트가 만들어지는 방식, 시점, 전략을 모두 다르게 할 수 있고 후처리 등 다양한 기능을 제공.
- 어플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공한다.
### 1.5.3 스프링 IoC의 용어 정리
- 빈<br> : 빈 또는 빈 오브젝트는 스프링이 IoC 방식으로 관리하는 오브젝트.
- 빈 팩토리<br> : 스프링의 IoC를 담당하는 핵심 컨테이너.
- 어플리케이션 컨텍스트<br> : 빈팩토리를 확장한 IoC컨테이너, 좀 더 확장된 관점에서 부를때 사용
- 설정정보 / 설정 메타정보<br> : 어플리케이션 컨텍스트, 빈 팩토리가 IoC를 적용하기위해 사용하는 메타정보
- 컨테이너 / IoC컨테이너<br> : IoC방식으로 빈을 관리한다는 의미에서 빈 팩토리, 어플리케이션 컨텍스트를 이렇게 부르기도 한다.

## 1.6 싱글톤 레지스트리와 오브젝트 스코프
#### 동일성(Identical)과 동등성(equivalent)
- 동일 == : 두 오브젝트가 동일하다면 사실상 하나의 오브젝트만 존재하는 것이고, 두 개의 래퍼런스 변수를 가지고 있을뿐.<Br>
- 동등 equals() : 동등한 두 오브젝트가 있지만 동일하지 않다면 두 개의 오브젝트가 메모리상에 존재하는것.

```java
DaoFactory factory = new DaoFactory();
UserDao userDao1 = factory.userDao();
UserDao userDao2 = factory.userDao();

System.out.println(userDao1);
System.out.println(userDao2);
```
실행결과
```shell
org.example.chapter1_tlswltjq.dao.UserDao@27d6c5e0
org.example.chapter1_tlswltjq.dao.UserDao@4f3f5b24
```
<br>
<br>

#### 어플리케이션 컨텍스트에 팩토리를 등록하고 수행해본 결과
```java
ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
UserDao dao3 = context.getBean("userDao", UserDao.class);
UserDao dao4 = context.getBean("userDao", UserDao.class);
System.out.println(dao3);
System.out.println(dao4);
```
결과실행
```shell
org.example.chapter1_tlswltjq.dao.UserDao@c03cf28
org.example.chapter1_tlswltjq.dao.UserDao@c03cf28
```
### 1.6.1 싱글톤 레지스트리로서의 애플리케이션 컨텍스트
### 1.6.2 싱글톤과 오브젝트의 상태
### 1.6.3 스프링 빈의 스콮,
## 1.7 의존관계 주입(DI)
### 1.7.1 제어의 역전(IoC)과 의존관계 주입
### 1.7.2 런타임 의존관계 설정
### 1.7.3 의존관계 검색과 주입
### 1.7.4 의존관계 주입의 응용
### 1.7.5 메소드를 이용한 의존관계 주입
## 1.8 XML을 이용한 설정
### 1.8.1 XML 설정
### 1.8.2 XML을 이용하는 애플리케이션 컨텍스트
### 1.8.3 DataSource 인터페이스로 변환
### 1.8.4 프로퍼티 값의 주입
## 1.9 정리