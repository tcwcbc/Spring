package proxcy;

/**
 * 타겟 클래스에 메소드 실행을 위임한 후에 
 * 부가기능을 추가하여 반환하는 메소드들을 정의한 클래스
 * 
 * 매 메소드마다 부가기능을 위한 코드가 중복됨 -> 
 *    InvocationHandler를 구현한 UppercaseHandler클래스를 이용
 * @author 최병철
 *
 */
public class HelloUppercase implements Hello{

	Hello hello;
	
	
	public HelloUppercase(Hello hello) {
		// TODO Auto-generated constructor stub
		this.hello = hello;
	}
	
	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayThankYou(name).toUpperCase();
	}

}
