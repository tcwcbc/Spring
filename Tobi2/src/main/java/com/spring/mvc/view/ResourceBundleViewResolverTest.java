package com.spring.mvc.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 여러종류의 뷰를 혼용하거나 뷰의 종류를 코드 밖에서 변경해줘야 하는경우 properties파일에 설정된 내용을 읽어와 실행
 * properties 파일로 관리하지 않고 ServletContext에 빈으로 등록하여 관리할 경우 order 값으로 우선순위를 지정하여 충돌을 회피
 * @author 최병철
 *
 */
public class ResourceBundleViewResolverTest extends AbstractDispatcherServletTest {
	@Test 
	public void rbvr() throws ServletException, IOException {
		setClasses(RBVR.class, InternalResourceViewResolver.class, HelloController.class, MainController.class);
		runService("/hello");
		assertThat(response.getForwardedUrl(), is("/WEB-INF/view/hello.jsp"));
		
		runService("/main");
		System.out.println(response.getForwardedUrl());
	}
	static class RBVR extends ResourceBundleViewResolver {{
		setOrder(1);
	}}
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello");
		}
	}
	@RequestMapping("/main")
	public static class MainController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("main.jsp");
		}
	}
}
