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
 * AnnotationController �׽�Ʈ Ŭ����
 * �ٸ� ��Ʈ�ѷ��� Ư�� �������̽��� �����ϸ� �ش� �������̽��� ������ HandlerAdapter�� ȣ���
 * �޼ҵ� ������ url��θ� �����ϰ� �Ǵµ� �ٸ� ��Ʈ�ѷ��� ����� ��캸�� �ڵ鷯�� ���� �پ��� ����.(@RequsetMapping)
 * @author �ֺ�ö
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
