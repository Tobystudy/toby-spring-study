# 2장 테스트
## 2.1 USERDAOTEST 다시 보기
### 2.1.1 테스트의 유용성
### 2.1.2 UserDaoTest의 특징
```java
package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao dao = context.getBean("userDao", UserDao.class);

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
        UserDao dao3 = context.getBean("userDao", UserDao.class);
        UserDao dao4 = context.getBean("userDao", UserDao.class);
        System.out.println(dao3);
        System.out.println(dao4);
    }
}

```
>가장 쉬운 접근법인 main메서드에 테스트 대상인 UserDao오브젝트를 호출하고 실제값을 직접 넣어 결과를 콘솔에 출력했다.

#### 웹을 통한 DAO테스트의 문제점
웹을 이용해 테스트하면?
- 아무런 기능이 없더라도 모든 레이어를 구현해야만 테스트를 실행해볼 수 있다.
- 테스트가 실패한다면 어느곳에서 에러가났는지 식별하기 쉽지않다.<br>(e.g.특정 레이어에서의 오류; 잘못된 api호출, 오타 등...)
>종합적으로 테스트를 진행하려던 것 이외의 모든게 영향을 준다.

#### 작은 단위의 테스트
대상이 명확하다면 그 대상만 테스트 할 수 있도록 하자<br>
작은 단위에 대해 테스트를 수행한 것 : Unit Test(단위 테스트)<br>
작게는 하나의 메소드부터 크게는 큰 기능들의 집합까지 모두 가능, 일반적으로 작은게 유리함. 목표는 다른 코드들이 신경쓰지않고 참여없이 테스트가 동작하는것

#### 작은 단위의 테스트를 자동수행되도록 코드는 만들어지는게 중요하다.
자동 수행되는 테스트의 장점 : 반복 쉬움.<br> 완성직전 수정이라도 테스트 코드가 준비되어 있다면 걱정없다.

### 2.1.3 UserDaoTest의 문제점
물론 UI까지 사용하는것 보다는 장점이 많았지만, 아직도 불편하다!
- 수동 확인 작업 : 결과를 확인하기위해 콘솔을 직접 봐야함. 콘솔로 결과를 보여줄 뿐, 아직 확인은 사람의 몫
- 실행 작업의 번거로움 : DAO종류가 수백 개 그 이상이 된다면?

## 2.2 USERDAOTEST 개선

### 2.2.1 테스트 검증의 자동화
### 2.2.2 테스트의 효율적인 수향고 결과 관리
## 2.3 개발자를 위한 테스팅 프레임워크
### 2.3.1 JUnit 테스트 실행 방법
### 2.3.2 테스트 결과의 일관성
기존 테스트는 실제 데이터 테이블에 삽입을 직접해서 검증했다.<br>
따라서 다음 테스트시 오류가 발생한다!.<br>
테스트가 외부상태에 따라 실패하는 이런 경우는 좋지 않은 테스트이다.<br>
항상 일관성 있는 결과를 보장하기위해 UserDao에 모든 레코드를 삭제하는 deleteAll(), getCount()메서드를 생성해보았다.<br>
이제 해당 메서드를 이용해 테스트를 작성하면 아직 미심쩍긴 하지만 항상 동일한 결과를 보여주는 테스트를 작성할 수 있다.
```java
public void deleteAll() throws SQLException{
    Connection c = dataSource.getConnection();

    PreparedStatement ps = c.preparedStatement("delete from users");
    ps.excuteUpdate();

    ps.close();
    c.close();
}
```

```java
public int getCount() throws SQLException{
    Connection c= dataSource.getConnection();
    PreparedStatement ps = c.preparedStatement("select count(*) from users");

    ResultSet rs = ps.excuteQuery();
    int count = rs.getInt(1);

    rs.close();
    ps.close();
    c.close();

}
```
### 2.3.3 포괄적인 테스트
### 2.3.4 테스트가 이끄는 개발
#### TDD
> 실패한 테스트를 성공시키기 위한 목적이 아닌 코드는 만들지 않는다.

테스트를 만들고 성공하도록 코드만 만드는식으로 진행.

- 이로인해 테스트 작성을 까먹지 않을 수 있다.
- 피드백을 매우 빠르게 받을 수 있다.
- 한편으로 마음에 여유를 준다.

#### JUnit이 테스트 클래스를 하나 실행하는 방식
1. 테스트 클래스에서 @Test가 붙은 public이며 void형이며 파라미터가 없는 테스트 메소드를 모두 찾는다.
2. 테스트 클래스의 오브젝트를 하나 생성한다.
3. @Before가 붙은 메소드가 있다면 실행한다.
4. @Test가 붙은 메소드를 하나 호출해 결과를 저장한다.
5. @After가 붙은 메소드가 있으면 실행한다.
6. 나머지 테스트에 대해서 2~5번을 실행한다.
7. 종합 결과를 돌려준다.

공유할 데이터가 있다면 인스턴스 변수를 이용할 것.

#### 픽스쳐
테스트에 필요한 정보와 오브젝트! UserDaoTest에서 "dao"

@Before에 두어 편리하게 사용하자.
### 2.3.5 테스트 코드 개선
## 2.4 스프링 테스트 적용
### 2.4.1 테스트를 위한 애플리케이션 컨텍스트 관리
### 2.4.2 DI와 테스트
## 2.5 학습 테스트로 배우는 스프링

### 2.5.1 학습 테스트의 장점
### 2.5.2 학습 테스트 예쩨
### 2.5.3 버그 테스트
## 2.6 정리