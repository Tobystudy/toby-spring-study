# 4.예외
## 4.1 사라진 SQLException
PreparedStatement.executeQuery()대신 템플릿 메소드를 적용하니 SQLException 을 던지지않도록 되었다.
```java
public void deleteAll() {  
    this.jdbcTemplate.update("delete from users");  
}
```
Jdbc템플릿은 내부적으로 executeUpdate()를 사용중
```java
protected int update(final PreparedStatementCreator psc, @Nullable final PreparedStatementSetter pss) throws DataAccessException {  
    this.logger.debug("Executing prepared SQL update");  
    return updateCount((Integer)this.execute(psc, (ps) -> {  
        boolean var9 = false;  
  
        Integer var4;  
        try {  
            ...
            int rows = ps.executeUpdate();  
            ...
        } finally {  
	        ...
        }  
	...
        return var4;  
    }, true));  
}
```
> 분명히 해당 메서드도 DataAccessException를 일으킬 수 있음을 명시하고있다.
> SQLExecption이 사라진건 OK, 그런데 DataAccessException는 ???
> 왜 명시하지않지?
### 4.1.1 초난감 예외처리
#### 아무런 처리를 하지않음
```java
try{
	...
}catch(SQLExecption e){
	//예외의 블랙홀
}
```
#### 단순 출력만함
```java
try{
	...
}catch(SQLExecption e){
	System.out.println(e);
}
```
```java
try{
	...
}catch(SQLExecption e){
	e.printStackTrace();
}
```
모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보돼야 한다.
#### 좋은 방법은 아니지만 처리를 하긴 함
```java
try{
	...
}catch(SQLExecption e){
	e.printStackTrace();
	System.exit(1);
}
```
#### 무의미하고 무책임한 throws
아몰랑 내 책임은 아닌듯 함
```java
public void method1() throws Execption{
	...
	method2();
	...
}
public void method2() throws Execption{
	...
	method3();
	...
}
public void method3() throws Execption{
	...
}
```
### 4.1.2 예외의 종류와 특징
#### Error
java.lang.Error의 서브클래스들, JVM에서 발생시키는것.
코드 레벨에서 잡을 수도 대응 방안도 없다.
시스템 레벨의 작업을 하는게 아니라면 신경쓸 필요도 없.
#### Execption
Checked Execption, Unchecked Execption 두 가지로 분류된다.
- Checked Execption :
  반드시 검사해야한다는 의미 try-catch-finally를 통해 반드시 에러 처리를 해야한다.
  e. g. ClassNotFoundExecption, IOExecption
- Unchecked Execption :
  코드레벨에서 예방가능하지만 개발자의 부주의로 인해 발생 -> 예상하지 못한 예외에서 발생하는것이 아니다.
  에러처리를 강제하지 않는다.
  런타임 에러들
  e.g. NullPointExecption, ArrayIndexOutOfBoundsException등...
### 4.1.3 예외 처리법
#### 1)예외복구
정상적인 작업흐름으로 자연스럽게 유도하기.
기능적으로 사용자에게 예외상황으로 비쳐도 애플리케이션에서는 정상적 설계 흐름을 따라 진행되야함.
#### 2)예외회피
예외처리를 호출한쪽에서 처리하도록 회피하는것. (throws를 이용)
앞서 예시로 본 catch블럭을 이용해 잡고 넘기는 회피가 아니다!
어떻게 되든 예외는 처리 되어야한다, 다만 처리를 내가 안할뿐...
예외 회피는 의도가 분명해야한다. 긴밀한 관계에 있는 다른 오브젝트에게 책임을 분명히 지게 하거나 자신을 사용하는 쪽에서 예외를 다루는게 최선이라는 확신이 있어야한다.
#### 3)예외전환 Execption transition
적절한 복구가 불가능할 때 적절한 예외로 전환해서 던지기.
일반적으로 두 가지 목적이 있다.
##### 3-1) 의미를 분명하게 하기위해
 API가 발생하는 기술적인 로우레벨을 상황에 적합한 의미를 가진 예외로 변경
전환하는 예외에 원래 발생한 예외를 담아 중첩 예외로 만드는 것이 좋다.
getCause()메소드를 이용해 처음 발생한 예외가 무엇인지 확인할 수 있기때문.
##### 3-2) 예외처리를 쉽고 단순하게 만들기위해 포장하는것
앞서 말한 중첩예외를 만들어 던지는 방식은 같다.
의미를 명확히 하려는 것이 아닌 체크 예외를 런타임(언체크) 예외로 바꾸는 경우에 사용

### 이제는 행방을 알 수 있다.
DataAccessException는 내부에서 처리되며 추상클래스인 NestedRuntimeException extends RuntimeException을 상속받고있다.
따라서 UnChecked이며 명시할 의무가 없었던것.

### 종속적인 SQLException
하나의 예외상황에서 기술에 따라 다른 예외코드를 발생/
기술에 종속적인 에러코드를 DB로부터 독립적 일 필요가 있다.
스프링을 사용하면 DataAccessException을 통해 계층적으로 추상화된 예외를 사용할 수 있다.
