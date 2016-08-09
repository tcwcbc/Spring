package com.spring.mvc.controllers;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * ������ SimpleController�� ���Ŭ����(�߻�Ŭ����)�� ������Ƽ ������ ���� �͵��� ���� �� �� �����θ� abstract�� ������ ���ø������� ���������
 * �̴� ����� �޾ƾ� �Ѵٴ� ������ �ֱ� ������ �̸� interface�� ����ϴ� ������ ����. �� �� �⺻������ �����Ǿ�� �ϴ� �׸���� ������̼��� Ȱ���Ͽ� ����
 * @author �ֺ�ö
 *
 */
public class HandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void simpleHandlerAdapter() throws ServletException, IOException {
		//����Ϳ� ��Ʈ�ѷ��� ������ ���
		setClasses(SimpleHandlerAdapter.class, HelloController.class);
		initRequest("/hello").addParameter("name", "Spring").runService();
		assertViewName("home");
		assertModel("message", "Hello Spring");
	}
	@Component("/hello")
	static class HelloController implements SimpleController {
		@ViewName("home")						//��� �� �̸� ����
		@RequiredParams({"name"})				//�Ķ���� �� ����
		
		/**
		 * HandlerAdapter�� ����Ŭ�������� ȣ�����
		 * ������ �ݹ�޼ҵ�
		 */
		public void control(Map<String, String> params, Map<String, Object> model) {
			model.put("message", "Hello " + params.get("name"));
		}
	}
	
	/**
	 * SimpleController�� DispatcherServlet�� ������ ȣ���Ű���� ���ִ� �����
	 * �������̽��� ���� ������ ������ �̿��� DispatcherServlet�� HandlerAdapterŸ���� ������Ʈ(����Ŭ����)�� ȣ����
	 * 
	 * �� �ڵ鷯 ����͸� ������ ����ϸ� DispatcherServlet�� Context�� ��ϵ� ����� supoorts �޼ҵ带 ȣ���Ͽ�
	 * ó���������� ���θ� ���� �����ϴٸ� �ش� ������� handler �޼ҵ�� ��Ʈ�ѷ��� ȣ���Ѵ�. (Adapter����)
	 * @author �ֺ�ö
	 *
	 */
	static class SimpleHandlerAdapter implements HandlerAdapter {
		/**
		 * �ش� �ڵ鷯 ����Ͱ� �����ϴ� ��Ʈ�ѷ����� Ȯ��, �ϳ��̻� ����
		 */
		public boolean supports(Object handler) {
			return (handler instanceof SimpleController);
		}

		/**
		 * �ڵ鷯�� �����ϴ� ���� �κ�
		 */
		public ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
			//Reflection Api�� ���� handler�� �޼ҵ带 ������
			Method m =  ReflectionUtils.findMethod(handler.getClass(), "control", Map.class, Map.class);
			//AnnotationUtilŬ������ ���� �ֳ����̼��� ���� ������ ���� ������
			ViewName viewName = AnnotationUtils.getAnnotation(m, ViewName.class);
			RequiredParams requiredParams = AnnotationUtils.getAnnotation(m, RequiredParams.class);
						
			//�ֳ����̼����� ������ �Ķ������ ���� ������ Map�� ����
			Map<String, String> params = new HashMap<String, String>();
			for(String param : requiredParams.value()) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			//������ ���ø� �ݹ� �޼ҵ�� SimpleController�� handler �޼ҵ带 ȣ��
			((SimpleController)handler).control(params, model);
				
			//��� �۾��� ������ HandlerAdapterŬ������ �������� ModelAndView ��ü�� ����.
			return new ModelAndView(viewName.value(), model);
		}
		
		/**
		 * �����ƴ��� Ȯ���ϴ� �޼ҵ�, ��Ʈ�ѷ��� getLastModified()���� ����
		 */
		public long getLastModified(HttpServletRequest request, Object handler) {
			return -1;
		}
	}
	/**
	 * CustomController Ŭ������ ������ ��Ʈ�ѷ��� ����Ͻ� ������ �� �޼ҵ带 �����ϴ� �������̽�
	 * 
	 * Map�� ���Ͽ� �Ķ���Ϳ� ModelAndView ��ü���� ����.
	 * @author �ֺ�ö
	 *
	 */
	public interface SimpleController {
		void control(Map<String, String> params, Map<String, Object> model);
	}
	
	/**
	 * View�� �̸��� �����ϴ� �ֳ����̼�
	 * �⺻������ ���ڿ��� �� �� �̸��� �־���� �Ѵ�.
	 * @author �ֺ�ö
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface ViewName {
		String value();
	}
	/**
	 * �ʼ� �Ķ���� �̸��� �������ִ� �ֳ����̼�.
	 * �Ķ������ �̸��� ������������ �迭�� ����
	 * @author �ֺ�ö
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface RequiredParams {
		String[] value();
	}
}
