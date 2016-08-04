package com.spring.mvc.hello;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;

/**
 * HelloController �׽�Ʈ
 * 
 * DispatcherServlet�� ��ӹ޾� �������� Ŭ������ ���Ͽ� ������ ���� ���ؽ�Ʈ�� ������ ��
 * ��������� ModelAndView ��ü�� ��ȯ �̸��� ���Ͽ� ����
 * @author �ֺ�ö
 *
 */
public class HelloControllerTest {
	
	@Test
	public void helloController() throws ServletException, IOException{
		//�������� DispatcherServlet
		ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
		//���� �׽�Ʈ�ϴ� Ŭ������ ��θ� ������ ����� ��ġ�� �ִ� �������ؽ�Ʈ�� ��ġ�� ����
		servlet.setRelativeLocations(getClass(), "servlet-context.xml");
		
		//��Ʈ���ؽ�Ʈ�� ���� �����ϴ� ��� �������ؽ�Ʈ�� ������ �׽�Ʈ�Ұ��̱� ������ �������ؽ�Ʈ ���ο� �� ����
		servlet.setClasses(HelloSpring.class);
		//MockServletConfig�� ���Ͽ� ����
		servlet.init(new MockServletConfig("spring"));
		
		//MockReq, MockRes�� ����
		MockHttpServletRequest req = new MockHttpServletRequest("GET","/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		servlet.service(req, res);
		
		//ModelAndView��ü�� ������ ��ȯ�ϴ� jsp�̸��� ������ �׽�Ʈ
		ModelAndView mv = servlet.getModelAndView();
		//���⼭�� �丮������ suffix,prefix�� �����߱� ������ jsp������ �̸��� ��ȯ��
		assertThat(mv.getViewName(), is("home"));
		assertThat((String)mv.getModel().get("message"), is("Hello Spring"));
	}
}
