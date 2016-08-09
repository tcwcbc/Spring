package com.spring.mvc.view;

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
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 리턴되는 뷰의 메시지 형식을 json으로 반환하는 기능 테스트
 * 
 * jackson라이브러리 사용
 * @author 최병철
 *
 */
public class JsonViewTeset extends AbstractDispatcherServletTest {
	@Test
	public void jsonView() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertThat(getContentAsString(), is("{\"messages\":\"Hello Spring\"}"));
	}
	
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		MappingJacksonJsonView jacksonJsonView = new MappingJacksonJsonView();
		
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("messages", "Hello " +req.getParameter("name"));
			
			return new ModelAndView(jacksonJsonView, model);
		}
	}
}
