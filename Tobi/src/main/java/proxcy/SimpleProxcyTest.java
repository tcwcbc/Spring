package proxcy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;



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
	
	@Test
	public void proxyFactoryBean(){
		//서비스 추상화 기술이 적용된 프록시 팩토리 빈
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		//부가 기능을 담은 어드바이스
		pfBean.addAdvice(new UppercaseAdvice());
		
		//프록시 가져오기
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
	}
	
	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		//메소드 이름으로 대상을 선정하는 포인트컷
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		//어드바이저 = 포인트컷(메소드 선정 알고리즘)+어드바이스(부가기능)
		//부가기능과 메소드를 하나로 묶어서 관리하기 위해 어드바이저로 만들어서 전달
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank you Toby"));
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
	}
	
	
	/**
	 * 프록시에 적용할 부가 기능
	 * 
	 * @author 최병철
	 *
	 */
	static class UppercaseAdvice implements MethodInterceptor{
		/**
		 * 타겟 오브젝트에 대한 정보도 알고있다. from pfBean
		 */
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// TODO Auto-generated method stub
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}
	
	@Test
	public void classNamePointcutAdvisor(){
		//포인트 컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
			public ClassFilter getClassFilter(){
				return new ClassFilter() {
					
					@Override
					public boolean matches(Class<?> arg0) {
						// TODO Auto-generated method stub
						return arg0.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*");
		
		//테스트
		
		//적용 클래스
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		//비적용 클래스
		class HelloWorld extends HelloTarget{};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		//적용 클래스
		class HelloToby extends HelloTarget{};
		checkAdviced(new HelloToby(), classMethodPointcut, true);
		
		
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced){
			//메소드 선정방식에서 필터링
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank you Toby"));
		} else{
			//어드바이스 적용 대상에서 아예 제외
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank you Toby"));
			
		}
		
	}
	
}
