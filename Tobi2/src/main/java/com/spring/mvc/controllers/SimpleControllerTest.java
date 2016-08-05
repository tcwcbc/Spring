package com.spring.mvc.controllers;
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

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * Controller �������̽��� ������ SimpleController�� ���ø����·� ��ӹ��� HelloController �׽�Ʈ
 * 
 * SimpleControllerHandlerAdapter�� �����ϴ� ��Ʈ�ѷ��� Controller �������̽��� �����ϱ⸸ �ϸ� ��
 * Controller �������̽��� �����Ͽ� �ߺ��ǰ� ������ ���� ������ �����Ʈ�ѷ��� �����
 * �̸� ����Ͽ� ��Ʈ�ѷ��� �����Ͻ� ������ �����ϰԲ� ����� �ȴ�.
 * 
 * �߰����� ����� getLastModified()�� ����Ϸ��� LastModified�������̽� ���� ����.
 * getLastModified() : ��� ���� ������ ����� �ð��� ���� ����ð��� ���Ͽ� �޶����� ������ ��Ʈ�ѷ� ����X ->
 * 								HTTP 304 Not Modified �ڵ带 ������. �ݺ��Ǵ� �������� ������� ������ ���ϸ� �ٿ��ִ� ���
 * @author �ֺ�ö
 *
 */
public class SimpleControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void helloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("home");
	}
	
	@Test(expected=Exception.class)
	public void noParameterHelloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello");
		runService();
	}
	
	@Test
	public void helloControllerUnitTest() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "Spring");
		Map<String, Object> model = new HashMap<String, Object>();
		
		new HelloController().control(params, model);
		
		assertThat((String)model.get("message"), is("Hello Spring"));
	}
	
	/**
	 * �׽�Ʈ�� ���� Servlet Mock ������Ʈ�� �غ����� �ʾƵ� �Ǿ� ��������
	 * @author �ֺ�ö
	 *
	 */
	@RequestMapping("/hello")
	static class HelloController extends SimpleController {		
		public HelloController() {
			this.setRequiredParams(new String[] {"name"});
			this.setViewName("home");
		}

		public void control(Map<String, String> params, Map<String, Object> model) throws Exception {
			model.put("message", "Hello " + params.get("name"));
		}
	}
	
	static abstract class SimpleController implements Controller {
		
		//�ʼ� �Ķ���͸� ����. �� ������Ƽ�� control()�� ����.
		private String[] requiredParams;
		private String viewName;
		
		public void setRequiredParams(String[] requiredParams) {
			this.requiredParams = requiredParams;
		}

		//Xml���� ���� �̸��� ������ �� �ִ� Setter
		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		final public ModelAndView handleRequest(HttpServletRequest req,
				HttpServletResponse res) throws Exception {
			
			//view�̸��� ���� �ȵǾ��� ��� ���ܹ߻�
			if (viewName == null) throw new IllegalStateException();

			//�Ķ���Ϳ� Map
			Map<String, String> params = new HashMap<String, String>();
			//�ʼ� �Ķ���͸� �ʿ� ��� ������ ���� �߻�
			for(String param : requiredParams) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}
			
			//�𵨿� Map
			Map<String, Object> model = new HashMap<String, Object>();
			
			this.control(params, model);
				
			//Controller Ÿ���� ��Ʈ�ѷ��� ��ȯ ���� ModelAndView ��ü
			return new ModelAndView(this.viewName, model);
		}
		
		//���� ��Ʈ�ѷ� ����
		public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
	}
}
