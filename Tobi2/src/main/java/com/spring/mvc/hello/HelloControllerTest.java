package com.spring.mvc.hello;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;

/**
 * HelloController 테스트
 * 
 * DispatcherServlet을 상속받아 재정의한 클래스를 통하여 서블릿이 서블릿 컨텍스트를 생성할 때
 * 만들어지는 ModelAndView 객체의 반환 이름을 비교하여 검증
 * @author 최병철
 *
 */
public class HelloControllerTest {
	
	@Test
	public void helloController() throws ServletException, IOException{
		//재정의한 DispatcherServlet
		ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
		//현재 테스트하는 클래스의 경로를 가지고 상대적 위치에 있는 서블릿컨텍스트의 위치를 지정
		servlet.setRelativeLocations(getClass(), "servlet-context.xml");
		
		//루트컨텍스트에 빈을 설정하는 대신 서블릿컨텍스트만 가지고 테스트할것이기 때문에 서블릿컨텍스트 내부에 빈 정의
		servlet.setClasses(HelloSpring.class);
		//MockServletConfig를 통하여 세팅
		servlet.init(new MockServletConfig("spring"));
		
		//MockReq, MockRes를 주입
		MockHttpServletRequest req = new MockHttpServletRequest("GET","/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		servlet.service(req, res);
		
		//ModelAndView객체를 가져와 반환하는 jsp이름을 가지고 테스트
		ModelAndView mv = servlet.getModelAndView();
		//여기서는 뷰리졸버에 suffix,prefix를 지정했기 때문에 jsp파일의 이름만 반환됨
		assertThat(mv.getViewName(), is("home"));
		assertThat((String)mv.getModel().get("message"), is("Hello Spring"));
	}
}
