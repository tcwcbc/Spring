package vol2.learningtest.spring.ioc.setcontext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 빈간의 의존관계 주입을 위한 설정파일
 * 
 * @author 최병철
 *
 */
@Configuration
public class Config {
	
	//@Bean이 붙은 메소드는 @Autowired가 붙은 일반 메소드처럼 동작
	@Bean
	public Hello hello(Printer printer){
		Hello hello = new Hello();
		hello.setName("Spring");
		//일종의 수동 DI
		hello.setPrinter(printer);
		return hello;
	}
	
	@Bean
	public Printer printer(){
		return new StringPrinter();
	}
}
