package com.spring.mvc.controllers;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * Controller 인터페이스를 구현한 SimpleController를 템플릿형태로 상속받은 HelloController 테스트
 * 
 * SimpleControllerHandlerAdapter가 지원하는 컨트롤러는 Controller 인터페이스를 구현하기만 하면 됨
 * Controller 인터페이스를 구현하여 중복되고 변경이 없는 내용은 기반컨트롤러로 만들고
 * 이를 상속하여 컨트롤러의 비지니스 로직만 구현하게끔 만들면 된다.
 * 
 * 추가적인 기능인 getLastModified()를 사용하려면 LastModified인터페이스 또한 구현.
 * getLastModified() : 결과 값이 마지막 변경된 시간과 기존 변경시간을 비교하여 달라진게 없으면 컨트롤러 실행X ->
 * 								HTTP 304 Not Modified 코드를 보내줌. 반복되는 페이지가 많은경우 서버의 부하를 줄여주는 기능
 * @author 최병철
 *
 */
public class SimpleControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void helloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("home");
	}
	
	@Test(expected=Exception.class)
	public void noParameterHelloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello");
		runService();
	}
	
	@Test
	public void helloControllerUnitTest() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "Spring");
		Map<String, Object> model = new HashMap<String, Object>();
		
		new HelloController().control(params, model);
		
		assertThat((String)model.get("message"), is("Hello Spring"));
	}
	
	/**
	 * 테스트를 위한 Servlet Mock 오브젝트를 준비하지 않아도 되어 간략해짐
	 * @author 최병철
	 *
	 */
	@RequestMapping("/hello")
	static class HelloController extends SimpleController {		
		public HelloController() {
			this.setRequiredParams(new String[] {"name"});
			this.setViewName("home");
		}

		public void control(Map<String, String> params, Map<String, Object> model) throws Exception {
			model.put("message", "Hello " + params.get("name"));
		}
	}
	
	static abstract class SimpleController implements Controller {
		
		//필수 파라미터를 정의. 이 프로퍼티만 control()로 전달.
		private String[] requiredParams;
		private String viewName;
		
		public void setRequiredParams(String[] requiredParams) {
			this.requiredParams = requiredParams;
		}

		//Xml에서 뷰의 이름을 지정할 수 있는 Setter
		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		final public ModelAndView handleRequest(HttpServletRequest req,
				HttpServletResponse res) throws Exception {
			
			//view이름이 지정 안되었을 경우 예외발생
			if (viewName == null) throw new IllegalStateException();

			//파라미터용 Map
			Map<String, String> params = new HashMap<String, String>();
			//필수 파라미터를 맵에 담고 없으면 예외 발생
			for(String param : requiredParams) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}
			
			//모델용 Map
			Map<String, Object> model = new HashMap<String, Object>();
			
			this.control(params, model);
				
			//Controller 타입의 컨트롤러는 반환 값이 ModelAndView 객체
			return new ModelAndView(this.viewName, model);
		}
		
		//실제 컨트롤러 로직
		public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
	}
}
