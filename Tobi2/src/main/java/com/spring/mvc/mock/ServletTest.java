package com.spring.mvc.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Servlet�׽�Ʈ
 * MockHttpServletRequest�� MockHttpServletResponse�� ����
 * Ŭ���̾�Ʈ�� ��û ���� �׽�Ʈ�� ����
 * @author �ֺ�ö
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
