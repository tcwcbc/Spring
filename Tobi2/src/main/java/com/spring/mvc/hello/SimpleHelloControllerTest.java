package com.spring.mvc.hello;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * HelloControllerTest Ŭ�������� ������ Mock������Ʈ�� �����Ͽ� �����ϰ� �����ϴ� ������ ���ø�����
 * AbstractDispatcherServletTest Ŭ�������� �����ϰ� ���������� �̿��Ͽ� �޼ҵ�ü�̴� ������� ����
 * 
 * @author �ֺ�ö
 *
 */
public class SimpleHelloControllerTest extends AbstractDispatcherServletTest{

	@Test
	public void helloController() throws ServletException, IOException{
							//�������� ���, �������� �̸��� ����ϸ� �׽�ƮŬ������ ����Ŭ������ ��
		ModelAndView mv = setRelativeLocations("servlet-context.xml")
				//�� Ŭ���� ���
				.setClasses(HelloSpring.class)
				//request url�� �޼ҵ� ���� ����, ����Ʈ�� GET�̹Ƿ� ��������
				.initRequest("/hello", RequestMethod.GET)
				//�Ķ���� ����
				.addParameter("name", "Spring")
				//�������ؽ�Ʈ ����(����)
				.runService()
				//ModelAndView ��ü ��������
				.getModelAndView();
		
		assertThat(mv.getViewName(), is("home"));
		assertThat((String)mv.getModel().get("message"), is("Hello Spring"));
	}
}
