package com.spring.mvc.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 서블릿 구현 클래스를 설정파일로 만들어서 web.xml에 등록하지 않고 MVC의 컨트롤러로 등록하여 빈으로 등록. 이렇게 하면
 * 커스터마이징이 가능해진다. 하지만, init()이나 destroy()와 같은 생명주기 메소드를 일일히 지정해줘야함.
 * 
 * Spring MVC가 제공하는 컨트롤러 4가지
 * 1. Servlet 타입		: MVC를 모르는 표준 servlet 타입이기 때문에 모델과 뷰를 반환X
 * 2. HttpRequest 타입	: 서블릿타입과 유사, Remote Method Invocation과 같은 로우레벨 기능사용하기 위함
 * 3. Controller 타입	: 어노테이션컨트롤러 이전엔 대표적인 컨트롤러였음. 
 * 						인터페이스 Controller -> 구현클래스 AbstractController -> 상속 MyController
 * 4. Annotated 타입		: 컨트롤러 클래스와 메소드에 붙인 애노테이션과 형식을 파악하여 매핑
 * 
 * SimpleServletHandlerAdapter를 제외한 나머지 핸들러어댑터는 디폴트 전략으로 등록이 되어 있으므로 따로 등록해줄 필요 X
 * @author 최병철
 *
 */
public class ServletControllerTest extends AbstractDispatcherServletTest {

	@Test
	public void helloServletController() throws UnsupportedEncodingException, ServletException, IOException {
		//핸들러어댑터와 서블릿컨트롤러를 자바코드를 이용해빈으로 등록
		setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class);
		
		initRequest("/hello").addParameter("name", "Spring");

		assertThat(runService().getContentAsString(), is("Hello Spring"));
	}

	@Named("/hello")
	static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			// Get 메소드 name 파라미터 값을 res에 담는다.
			String name = req.getParameter("name");
			resp.getWriter().print("Hello " + name);
		}
	}
}
