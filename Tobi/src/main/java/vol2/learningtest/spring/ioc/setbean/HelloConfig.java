package vol2.learningtest.spring.ioc.setbean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vol2.learningtest.spring.ioc.setcontext.Hello;
import vol2.learningtest.spring.ioc.setcontext.Printer;
import vol2.learningtest.spring.ioc.setcontext.StringPrinter;

/**
 * 하나의 빈이 내부에 다른 빈을 직접 호출하는 CTX
 * new로 생성하여 리턴해도 @Configuration 애노테이션의 디폴트 스코프때문에 싱글톤
 * @author 최병철
 *
 */
@Configuration
public class HelloConfig {
	@Bean
	public Hello hello(){
		Hello hello = new Hello();
		hello.setName("Spring");
		hello.setPrinter(printer());
		return hello;
	}
	@Bean
	public Hello hello2(){
		Hello hello = new Hello();
		hello.setName("Spring2");
		hello.setPrinter(printer());
		return hello;
	}
	
	@Bean
	public Printer printer(){
		return new StringPrinter();
	}
}
