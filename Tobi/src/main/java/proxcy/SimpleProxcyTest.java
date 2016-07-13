package proxcy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class SimpleProxcyTest {
	
	@Test
	public void simpleProxy(){
		
		//프록시를 사용
		Hello hello = new HelloTarget();
		
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank you Toby"));
		
		//다이나믹 프록시를 사용
		Hello proxieHello = (Hello)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Hello.class}, 
				new UppercaseHandler(new HelloTarget()));
		assertThat(proxieHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		assertThat(proxieHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxieHello.sayHi("Toby"), is("HI TOBY"));
		
		
	}
	
}
