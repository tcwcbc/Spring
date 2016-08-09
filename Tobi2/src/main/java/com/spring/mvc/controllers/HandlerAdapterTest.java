package com.spring.mvc.controllers;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 기존의 SimpleController는 기반클래스(추상클래스)로 프로퍼티 설정과 같은 것들을 정의 한 후 구현부만 abstract로 선언한 템플릿패턴을 사용했지만
 * 이는 상속을 받아야 한다는 제약이 있기 때문에 이를 interface를 사용하는 것으로 변경. 이 때 기본적으로 설정되어야 하는 항목들을 어노테이션을 활용하여 세팅
 * @author 최병철
 *
 */
public class HandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void simpleHandlerAdapter() throws ServletException, IOException {
		//어댑터와 컨트롤러를 빈으로 등록
		setClasses(SimpleHandlerAdapter.class, HelloController.class);
		initRequest("/hello").addParameter("name", "Spring").runService();
		assertViewName("home");
		assertModel("message", "Hello Spring");
	}
	@Component("/hello")
	static class HelloController implements SimpleController {
		@ViewName("home")						//대상 뷰 이름 지정
		@RequiredParams({"name"})				//파라미터 명 지정
		
		/**
		 * HandlerAdapter의 구현클래스에서 호출당함
		 * 일종의 콜백메소드
		 */
		public void control(Map<String, String> params, Map<String, Object> model) {
			model.put("message", "Hello " + params.get("name"));
		}
	}
	
	/**
	 * SimpleController와 DispatcherServlet을 연결해 호출시키도록 해주는 어댑터
	 * 인터페이스를 통한 유연한 구현을 이용해 DispatcherServlet은 HandlerAdapter타입의 오브젝트(구현클래스)를 호출함
	 * 
	 * 이 핸들러 어댑터를 빈으로 등록하면 DispatcherServlet은 Context에 등록된 빈들의 supoorts 메소드를 호출하여
	 * 처리가능한지 여부를 묻고 가능하다면 해당 어댑터의 handler 메소드로 컨트롤러를 호출한다. (Adapter패턴)
	 * @author 최병철
	 *
	 */
	static class SimpleHandlerAdapter implements HandlerAdapter {
		/**
		 * 해당 핸들러 어댑터가 지원하는 컨트롤러인지 확인, 하나이상 가능
		 */
		public boolean supports(Object handler) {
			return (handler instanceof SimpleController);
		}

		/**
		 * 핸들러가 수행하는 로직 부분
		 */
		public ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
			//Reflection Api를 통해 handler의 메소드를 가져옴
			Method m =  ReflectionUtils.findMethod(handler.getClass(), "control", Map.class, Map.class);
			//AnnotationUtil클래스를 통해 애노테이션을 통해 세팅한 값을 가져옴
			ViewName viewName = AnnotationUtils.getAnnotation(m, ViewName.class);
			RequiredParams requiredParams = AnnotationUtils.getAnnotation(m, RequiredParams.class);
						
			//애노테이션으로 지정한 파라미터의 값을 가져와 Map에 저장
			Map<String, String> params = new HashMap<String, String>();
			for(String param : requiredParams.value()) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			//일종의 템플릿 콜백 메소드로 SimpleController의 handler 메소드를 호출
			((SimpleController)handler).control(params, model);
				
			//모든 작업이 끝나면 HandlerAdapter클래스는 서블릿에서 ModelAndView 객체를 전달.
			return new ModelAndView(viewName.value(), model);
		}
		
		/**
		 * 수정됐는지 확인하는 메소드, 컨트롤러의 getLastModified()에서 설정
		 */
		public long getLastModified(HttpServletRequest request, Object handler) {
			return -1;
		}
	}
	/**
	 * CustomController 클래스가 구현할 컨트롤러의 비즈니스 로직이 들어갈 메소드를 포함하는 인터페이스
	 * 
	 * Map을 통하여 파라미터와 ModelAndView 객체들을 전달.
	 * @author 최병철
	 *
	 */
	public interface SimpleController {
		void control(Map<String, String> params, Map<String, Object> model);
	}
	
	/**
	 * View의 이름을 지정하는 애노테이션
	 * 기본값으로 문자열로 된 뷰 이름을 넣어줘야 한다.
	 * @author 최병철
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface ViewName {
		String value();
	}
	/**
	 * 필수 파라미터 이름을 정의해주는 애노테이션.
	 * 파라미터의 이름은 여러개임으로 배열로 선언
	 * @author 최병철
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface RequiredParams {
		String[] value();
	}
}
