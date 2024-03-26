# 3장 템플릿
개방 폐쇄 원칙에 대해서 다시 생각해보자.
확장에는 열려있고 수정에는 닫혀있다. => 코드의 부분은 변치 않으려하거나, 기능이 다양해지고 확장되려는 부분이 있다.
각각 다른 목적과 이유에 의해 다른 시점에 독립적으로 변경될 수 있는 "효율적"구조를 만들어 주는것.
템플릿은 바뀌는 성질이 다른 코드중 변경이 거의 일어나지 않고 일정한 패턴을 유지되는 특성을 가진 부분을 자유롭게 변경되는 성질을 가진 부분으로 독립시키는 방법이다.
## 3.1 다시 보는 초난감 DAO
```java
public void deleteAll() throws SQLException{
	Connection c = dataSource.getConnection();

	//예외 발생 가능지점
	PreparedStatement ps = c.prepareStatement("delete from users");
	ps.executeUpdate();

	ps.close();
	c.close();
}
```

두 개의 리소스 Connection과 PreparedStatement를 사용한다.
 
 >executeUpdate  -> Exection발생한다면?
 
 Connection, PreparedStatement 두 리소스 반납 불가능, 리소스가 모자라 서버 중단 가능성
##### 어떻게 해결할것인가?
>- 반드시 close()를 실행해 리소스를 반납하게한다
>- try/catch/finally를 사용해보자.

```java
public void deleteAll() throws SQLException{
	Connection c =null;
	PreparedStatement ps = null;

	try{
		c = dataSource.getConnection();
		ps = c.prepareStatement("delete from users");
		ps.executeUpdate();
	}catch (SQLExecption e) {
		throw e;
	}finally{
		if(ps != null){
			try{
				ps.close();
			} catch (SQLExecption e){
			}
		}
		if(c != null){
			try{
				c.close();
			} catch (SQLExecption e){
			}
		}
	}
}
```

>이제 반드시 리소스를 반환하게 된다!
#### 조회 기능의 예외처리
``` java
public void getCount() throws SQLExection{
...
	
	try{
	...
	//결과 반환을 위해 ResultSet이 추가로 필요하다.
		ResultSet rs = null
	} catch(SQLExection e){
		throw e;
	}finally{
		if(rs != null){
			try{
				c.close();
			} catch (SQLExecption e){
			}
		}
	...
	}
	...

}

```
> 조회기능도 이제 반드시 리소스를 반환해 안전하게 예외를 처리해 줄 수 있다.

#### 일단은 안전한 코드가 되었음.
## 3.2 변하는 것과 변하지 않는 것
### JDBC try/catach/finally의 문제점
#### 새로운 메소드를 만들려면?
1. ResultSet의 유무에 따라 두 종류가 있으니 두 가지를 복사해 둔다.
2. 필요에따라 Crtl + c , Ctrl + v, 그리고 수정한다.
#### 휴먼에러 발생가능성 매우 높아짐
> e.g. 부분을 뺴고 복사, 수정도중 필수 코드블럭을 지워버림

#### 에러를 찾기도 어렵다.
>제일 찾기 어려운 에러가 오타 찾기

#### 테스트 코드를 작성하기 어렵다?
p.215 특별히 개발한 Connection, PreparedStatement구현 클래스 필요?
감은 오는데 커넥션과 SQL 예외에대해서 이해가 떨어져서  예외상황에서의 두 객체의 형태가 감이 잘 안온다.

화자의 의도는 파악함
#### 그래서 어케함?
### 분리와 재사용을 위한 디자인 패턴 적용
이전에 해본대로 자주 바뀌는 부분을 분리해보자.

```java
public void deleteAll() throws SQLException{
	Connection c =null;
	PreparedStatement ps = null;

	try{
		c = dataSource.getConnection();

		//deleteAll메소드의 경우 아래 이 문장만 자주 바뀜
		ps = c.prepareStatement("delete from users");
		
		ps.executeUpdate();
	}catch (SQLExecption e) {
		throw e;
	}finally{
		if(ps != null){
			try{
				ps.close();
			} catch (SQLExecption e){
			}
		}
		if(c != null){
			try{
				c.close();
			} catch (SQLExecption e){
			}
		}
	}
}
```

#### 메소드를 추출해보자
```java
public void deleteAll() throws SQLException{
	...
	try{
		c = dataSource.getConnection();

		ps = makeStatement(c);
		
		ps.executeUpdate();
	}catch (SQLExecption e) {
		...
	}
	private PreparedStatement makeStatement(Conenction c) throws SQLExecption {
		PreparedStatement ps = null;
		ps = c.prepareStatement("delete from users");
		return ps;
	}
}
```

>메소드로 묶으면 보통 재사용이 되야하는데 지금 이상태에서는 DAO마다 로직을 새로 확장해줘야함. <br>재사용 불가능

#### 템플릿 메소드 패턴 적용
>상속을 통해 기능을 확장해서 사용하는 방법!
>변하지 않는 부분을 슈퍼클래스에 두고 변하는 부분은 추상메서드화
>서브클래스에서 오버라이드 하여 사용하게 한다.

1. UserDAO를 추상클래스로선언
2. makeStatement()를 추상 메소드로 선언
3. UserDAO를 상속하는 서브클래스 UserDaoDeleteAll (예시)를 선언
4. UserDaoDeleteAll에서 makeStatement를 구현.
#### 확장은 자유로워 졌지만
- DAO로직마다 새로운 클래스를 생성해야한다.
- 확장 구조가 클래스 설계시점에서 고정되어버린다.

#### 전략 패턴 적용
>다양한 로직들, 앞선 경우라면 UserDAO를 상속받아 매번 구현해야하는 구현체들을 아얘 별도의 오브젝트로 분리하고 인터페이스를 통해 의존하도록 만드는 전략

```java
public void deleteAll() throws SQLExecption{
	...
	try{
		c = dataSource.getConnection();

		StatementStrategy strategy = new DeleteAllStatement();
		ps = strategy.makePreparedStatement(c);

		ps.excuteUpdate();
	} catch (SQLException e){
	...
}
```
위와같이 StatementStrategy인터페이스를 의존해 컨텍스트를 유지하고(폐쇄의 원칙) 전략을 바꿀 수 있다(개방의 원칙)
그렇지만 단순히 전략 패턴을 따라가다보니 특정 구현체를 알고 있어야 하게 되었다.
이건 좋지 못하다.
#### 그래서 또 어떻게 해결해야되나
전략은 클라이언트로부터 받아오는것이 좋다. 클라이언트가 필요한 전략에 따라 오브젝트를 생성하고 컨텍스트는 해당 오브젝트를 사용하는것이다.

 #### ? 
 근데 이거 해본거
 >생성자를 수정하고
```java
public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
```
>팩토리를 만들고
```java
public class DaoFactory {
    public UserDao userDao(){
        ConnectionMaker connectionMaker = new DConnectionMaker();
        return new UserDao(connectionMaker);
    }
}
```
>팩토리로부터 필요한 객체를 주입받았다.

#### 컨텍스트에 해당하는 부분을 별도의 메소드로
```java
public void jdbcContextWithStatementStarategy(StatementStrategy stmt) throws SQLExecption{
	Connection c = null;
	PreparedStatement ps = null;

	try{
		c = dataSource.getConnection();

		//메소드가 인터페이스에 의존하며 전략에 따라 여러 알고리즘을 수행할 것이다.
		ps = stmt.makePreparedStatement(c);

		ps.executeUpdate();
	} catch(SQLExecption e){
		throw e;
	} finally {
		if(ps != null) {try{ps.close();} catch(SQLException e {})}
		if(c != null) {try{c.close();} catch(SQLException e {})}
	}
}
```

#### 전략을 정할 클라이언트(deleteAll() 메소드)
```java
public void deleteAll() throws SQLException{
	StatementStrategy st = new DeleteAllStatement();
	jdbcContextWithStatementStarategy(st);
}
```

### 여기까지 완성된 시점에서 문제점
1. 클래스가 많아졌다. -> 런타임에 DI가 다이나믹하게 이루어짐 좋긴한데 템플릿 패턴보다 그닥?
2. DAO에서 전략 오브젝트를 받아올때 부가적인 정보가 필요하다면 이를 저장할 변수가 또 필요하다
#### 해결 방법
1. 특정 클래스와 메소드에 강하게 결합되어있으니 로컬 클래스로 이전해 중첩 클래스로 작성한다.
2. 하나의 메소드에만 사용된다면 익명 내부 클래스로 선언한다.
### JdbcContext의 DI
JdbcContext는 인터페이스가 아니라 구현체이다. 이제껏 구현체에 의존하지않도록 리팩터링 해왔는데 과연 괜찮을까?
제어권한을 오브젝트 밖으로 분리했다는점에서 넓게 DI를 적용했다고 볼 수 있다.
다음 3가지 이유로 jdbccontext와 userDAO를 DI구조로 만들어야한다.
??? 이해 잘 못함

### 코드를 이용한 DI
강한 결합관계일 경우 DAO마다 JdbcContext를가지게 할 수 있다.
이 떄 싱글톤을 포기해야한다 관계가 드러나지않는 장점이 있지만 싱글톤으로 관리할 수 없고 부가적인 코드가 필요하다는 단점이 있다(생성자를 통해 직접 DI하니까)

## 템플릿과 콜백
- 템플릿 : 틀 -> 템플릿 메소드 패턴  : 고정된 틀의 로직을 슈퍼 클래스에
- 콜백 : 실행되는것을 목적으로 다른오브젝트의 메소드에 전달되는 오브젝트<br>자바에서 메소드 자체를 파라미터로 전달할 방법이 없기 때문에 메소드가 담긴 오브젝트를 전달해야함.

#### 콜백의 분리
>아래 코드에 사용된 콜백 오브젝트는 하나의 고정된 쿼리를 담아 PreparedStatement객체를 만들뿐이다. 쿼리만 변동 될 가능성이 매우 높다 분리해 보자
```java
public void deleteAll() throws SQLException {
		jdbcContextWithStatementStrategy(
			new StatementStrategy() {
				public PreparedStatement makePreparedStatement(Connection c)
						throws SQLException {
					return c.prepareStatement("delete from users");
				}
			}
		);
	}
```

```java
public void deleteAll() throwsSQLExecption{
	excuteSQL("delete from users");
}
```

``` java
public void excuteSQL(final String query) throws SQLException {
		jdbcContextWithStatementStrategy(
			new StatementStrategy() {
				public PreparedStatement makePreparedStatement(Connection c)
						throws SQLException {
					return c.prepareStatement(query);
				}
			}
		);
	}
```

분리된 콜백은 재사용할 수 있다 템플릿에도 적용가능!