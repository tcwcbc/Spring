package com.spring.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 컨트롤러에서 특정 뷰의 이름을 지정하지 않고 ModelAndView를 반환하였을 때 
 * 요청Url을 분석하여 뷰의 이름을 자동으로 생성해주는 기능 테스트
 * @author 최병철
 *
 */
public class RequestViewNameTranslatorTest extends AbstractDispatcherServletTest {
	@Test
	public void defaultRequestToViewNameTranslatorTest() throws ServletException, IOException {
		setClasses(Config1.class);
		runService("/hello").assertViewName("hello.jsp");
		runService("/hello/world").assertViewName("hello/world.jsp");
		runService("/hi").assertViewName("hi.jsp");
	}
	@Configuration static class Config1 {
		@Bean public HandlerMapping handlerMapping() {
			return new BeanNameUrlHandlerMapping() {{
				this.setDefaultHandler(defaultHandler());
			}};
		}
		@Bean public RequestToViewNameTranslator viewNameTranslator() {
			return new DefaultRequestToViewNameTranslator() {{
				this.setSuffix(".jsp");
			}};
		}
		@Bean public Controller defaultHandler() {
			return new Controller() {
				public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
					return new ModelAndView();
				}
			};
		}
	}
}
