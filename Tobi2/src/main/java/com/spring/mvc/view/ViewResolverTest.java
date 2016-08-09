package com.spring.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * ViewResolver의 suffix와 prefix를 설정하여 논리적인 뷰의 이름만 반환하여 포워딩함
 * @author 최병철
 *
 */
public class ViewResolverTest extends AbstractDispatcherServletTest {
	@Test
	public void jstlView() throws ServletException, IOException {
		setRelativeLocations("jstlview.xml").setClasses(HelloController.class);
		runService("/hello");
		System.out.println(this.response.getForwardedUrl());
	}
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello").addObject("message", "Hello Spring");
		}
	}
	
}
