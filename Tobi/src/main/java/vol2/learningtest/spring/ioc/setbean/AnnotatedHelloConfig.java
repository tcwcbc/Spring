package vol2.learningtest.spring.ioc.setbean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 빈 설정 메타정보를 담고있는 클래스
 * @Configuration 	애노테이션을 붙이면 빈 메소드 안에서 new로 생성하여 반환하여도
 * 					싱글톤 팩토리 빈이 적용되지만 붙이지 않으면 싱글톤 적용X
 * 					메타 애노테이션으로 @Component가 있기에 이 클래스 자체도 빈으로 등록됨.
 * @author 최병철
 *
 */
@Configuration
public class AnnotatedHelloConfig {

	@Bean
	public AnnotatedHello annotatedHello(){
		return new AnnotatedHello();
	}
}
