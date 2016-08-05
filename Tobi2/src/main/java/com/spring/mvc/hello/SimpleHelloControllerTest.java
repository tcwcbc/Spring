package com.spring.mvc.hello;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * HelloControllerTest 클래스에서 일일히 Mock오브젝트를 생성하여 주입하고 설정하는 과정을 템플릿으로
 * AbstractDispatcherServletTest 클래스에서 구현하고 빌더패턴을 이용하여 메소드체이닝 방식으로 설정
 * 
 * @author 최병철
 *
 */
public class SimpleHelloControllerTest extends AbstractDispatcherServletTest{

	@Test
	public void helloController() throws ServletException, IOException{
							//설정파일 등록, 설정파일 이름만 등록하면 테스트클래스가 기준클래스가 됨
		ModelAndView mv = setRelativeLocations("servlet-context.xml")
				//빈 클래스 등록
				.setClasses(HelloSpring.class)
				//request url과 메소드 형식 지정, 디폴트는 GET이므로 생략가능
				.initRequest("/hello", RequestMethod.GET)
				//파라미터 주입
				.addParameter("name", "Spring")
				//서블릿컨텍스트 생성(실행)
				.runService()
				//ModelAndView 객체 가져오기
				.getModelAndView();
		
		assertThat(mv.getViewName(), is("home"));
		assertThat((String)mv.getModel().get("message"), is("Hello Spring"));
	}
}
