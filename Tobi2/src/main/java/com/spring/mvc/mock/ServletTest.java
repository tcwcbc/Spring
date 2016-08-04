package com.spring.mvc.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Servlet테스트
 * MockHttpServletRequest와 MockHttpServletResponse를 통한
 * 클라이언트의 요청 없이 테스트를 수행
 * @author 최병철
 *
 */
public class ServletTest {
	@Test
	public void getMethodServlet() throws ServletException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		SimpleGetServlet servlet = new SimpleGetServlet();
		servlet.service(req, res);
		servlet.init();
		
		assertThat(res.getContentAsString(), is("<HTML><BODY>Hello Spring</BODY></HTML>"));
		assertThat(res.getContentAsString().indexOf("Hello Spring") > 0, is(true));
	}
	

}
