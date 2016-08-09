package com.spring.mvc.controllers;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * AnnotationController 테스트 클래스
 * 다른 컨트롤러는 특정 인터페이스를 구현하면 해당 인터페이스와 연관된 HandlerAdapter가 호출됨
 * 메소드 단위로 url경로를 지정하게 되는데 다른 컨트롤러를 사용할 경우보다 핸들러의 수가 줄어드는 장점.(@RequsetMapping)
 * @author 최병철
 *
 */
public class SimpleAnnotationControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void annotationHello() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("home");
	}

	@Controller	
	static class HelloController {
		@RequestMapping("/hello")
		public String hello(@RequestParam("name") String name, ModelMap map) {
			map.put("message", "Hello " + name);
			return "home";
		}
	}
}
