# 2장 테스트

## 2.1 UserDaoTest 다시 보기
### 2.1.1 테스트의 유용성
### 2.1.2 UserDaoTest의 특징
```
public class UserDaoTest {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("user");
        user.setName("강태찬");
        user.setPassword("unmarried");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");    
    }
}
```

- 위 테스트 코드는 자바에서 쉽게 실행 가능한 `main() 메소드`를 이용한다.
- 테스트할 대상인 UserDao의 오브젝트를 가져와 메소드를 호출한다.
- 테스트에 사용할 입력 값(User 오브젝트)을 `직접 코드에서 만들어` 넣어준다.
- 테스트의 결과를 `콘솔`에 출력해준다.
- 각 단계의 작업이 에러 없이 끝나면 콘솔에 성공 메시지로 출력해준다.
<br/>

- 위 테스트 방법에서 가장 돋보이는 것은 main() 메소드를 이용해 쉽게 테스트 수행을 가능하게 했다는 점과 테스트 대상인 UserDao를 직접 호출해서 사용한다는 점이다.

#### 웹을 통한 DAO 테스트 방법의 문제점
- 보통 웹 프로그램에서 DAO를 테스트하는 방법은 서비스 계층, MVC 프레젠테이션 계층까지 포함한 모든 입출력 기능을 만들고 테스트용 웹 애플리케이션을 서버에 배치한 뒤, 웹 화면을 띄워보는 것이다.
- 하지만 DAO만을 테스트하기 위해서 이렇게 테스트 환경을 구성하는 것은 너무나도 공수가 많이든다.
- DAO뿐만 아니라 서비스 클래스, 컨트롤러, JSP 뷰 등 모든 레이어의 기능을 다 만들고 나서야 테스트가 가능하다는 점이 큰 문제다.
<br/>

- 테스트가 실패했다면, 어디에서 문제가 발생했는지를 찾아내야 하는 수고도 필요하다.
- 폼을 띄우고 값을 입력하고 저장 버튼을 눌렀는데 에러가 발생했다고 가정해보자. 에러 메시지와 호출 스택만 보고 간단하게 원인을 찾아낼 수 있는가?
- DB 연결이 원인일 수도 있고, DAO 코드가 잘못되었을 수도 있고, JDBC API를 잘못 호출해서일 수도 있다.
- `정작 테스트할 DAO의 문제가 아니라 서버환경에서 웹 화면을 통해 DAO를 테스트하려고 만든 다른 코드 때문에 에러`가 났을 수 있다.
- 사실 테스트하고 싶었던 것은 UserDao였는데 다른 계층의 코드와 컴포넌트, 심지어 서버의 설정 상태까지 모두 테스트에 영향을 줄 수 있기 때문에 이런 방식으로 테스트하는 것은 번거롭다.
