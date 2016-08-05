package com.spring.mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;

import org.junit.After;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * DispatcherServlet �׽�Ʈ�� ���� ��� Ŭ����
 * �׽�Ʈ�� ���� �� Ŭ������ ��ӹ޴´�.
 * @author �ֺ�ö
 *
 */
public abstract class AbstractDispatcherServletTest implements AfterRunService {
	
	//�� ������Ʈ, ��ӹ��� �ڽ�Ŭ�������� ������ �����ϵ��� protected
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	protected MockServletConfig config = new MockServletConfig("spring");
	protected MockHttpSession session;
	protected ConfigurableDispatcherServlet dispatcherServlet;
	
	//������ ���� Ŭ����, ������, ����θ� �̿�
	private Class<?>[] classes;
	private String[] locations;
	private String[] relativeLocations;
	private String servletPath;
	
	/**
	 * ������ ����
	 * @param locations	������
	 * @return			��������
	 */
	public AbstractDispatcherServletTest setLocations(String ...locations) {
		this.locations = locations;
		return this;
	}
	
	/**
	 * ����� ����
	 * @param relativeLocations	�����
	 * @return					��������
	 */
	public AbstractDispatcherServletTest setRelativeLocations(String ...relativeLocations) {
		this.relativeLocations = relativeLocations;
		return this;
	}
	
	/**
	 * �� ���
	 * @param classes	����� ��
	 * @return			��������
	 */
	public AbstractDispatcherServletTest setClasses(Class<?> ...classes) {
		this.classes = classes;
		return this;
	}
	
	/**
	 * ���� ���ؽ�Ʈ ��� ����
	 * @param servletPath	���� ���ؽ�Ʈ ���
	 * @return				��������
	 */
	public AbstractDispatcherServletTest setServletPath(String servletPath) {
		if (this.request == null)
			this.servletPath = servletPath;
		else
			this.request.setServletPath(servletPath);
		return this;
	}

	/**
	 * Request �ʱ�ȭ
	 * @param requestUri	��û���
	 * @param method		��û �޼ҵ� �̸�
	 * @return				��������
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri, String method) {
		this.request = new MockHttpServletRequest(method, requestUri);
		this.response = new MockHttpServletResponse();
		if (this.servletPath != null) this.setServletPath(this.servletPath);
		return this;
	}
	
	/**
	 * Request �ʱ�ȭ
	 * @param requestUri	��û���
	 * @param method		��û �޼ҵ�(Enum)
	 * @return				��������
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri, RequestMethod method) {
		return this.initRequest(requestUri, method.toString());
	}
	
	/**
	 * Request �ʱ�ȭ
	 * @param requestUri	��û���
	 * @return				��������
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri) {
		initRequest(requestUri, RequestMethod.GET);
		return this;
	}
	
	/**
	 * �Ķ���� �߰�
	 * @param name		�Ķ���� ��
	 * @param value		�Ķ���� ��
	 * @return			��������
	 */
	public AbstractDispatcherServletTest addParameter(String name, String value) {
		if (this.request == null) 
			throw new IllegalStateException("request�� �ʱ�ȭ���� �ʾҽ��ϴ�.");
		this.request.addParameter(name, value);
		return this;
	}
	
	/**
	 * DispatcherServlet ����
	 * @return					��������
	 * @throws ServletException	IllegalStateException���� ����
	 */
	public AbstractDispatcherServletTest buildDispatcherServlet() throws ServletException {
		if (this.classes == null && this.locations == null && this.relativeLocations == null) 
			throw new IllegalStateException("classes�� locations �� �ϳ��� �����ؾ� �մϴ�");
		this.dispatcherServlet = createDispatcherServlet();
		this.dispatcherServlet.setClasses(this.classes);
		this.dispatcherServlet.setLocations(this.locations);
		if (this.relativeLocations != null)
			this.dispatcherServlet.setRelativeLocations(getClass(), this.relativeLocations);
		this.dispatcherServlet.init(this.config);
		
		return this;
	}
	
	/**
	 * DispatcherServlet �������� ����
	 * @return	ConfigurableDispatcherServlet
	 */
	protected ConfigurableDispatcherServlet createDispatcherServlet() {
		return new ConfigurableDispatcherServlet();
	}

	/**
	 * ���� ����(Context ����)
	 * @return	AfterRunService �������̽��� ������ AbstractDispatcherServletTestŬ���� 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService() throws ServletException, IOException {
		if (this.dispatcherServlet == null) buildDispatcherServlet(); 
		if (this.request == null) 
			throw new IllegalStateException("request�� �غ���� �ʾҽ��ϴ�");
		this.dispatcherServlet.service(this.request, this.response);
		return this;
	}
	
	/**
	 * ���� ����(Context ����)
	 * @param requestUri	��û Uri 
	 * @return				AfterRunService �������̽��� ������ AbstractDispatcherServletTestŬ���� 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService(String requestUri) throws ServletException, IOException {
		initRequest(requestUri);
		runService();
		return this;
	}
	
	/**
	 * ���� ����(Context ����)
	 * @param requestUri	��û Uri 
	 * @param method		��û �޼ҵ� �̸�
	 * @return				AfterRunService �������̽��� ������ AbstractDispatcherServletTestŬ���� 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService(String requestUri, String method) throws ServletException, IOException {
		initRequest(requestUri, method);
		runService();
		return this;
	}
	
	/**
	 * ���ؽ�Ʈ ��ü�� ��ȯ�ϴ� �޼ҵ�
	 */
	public ConfigurableWebApplicationContext getContext() {
		if (this.dispatcherServlet == null) 
			throw new IllegalStateException("DispatcherServlet�� �غ���� �ʾҽ��ϴ�");
		return (ConfigurableWebApplicationContext)this.dispatcherServlet.getWebApplicationContext();
	}
	
	/**
	 * DL�� ���� �޼ҵ�
	 */
	public <T> T getBean(Class<T> beanType) {
		if (this.dispatcherServlet == null) 
			throw new IllegalStateException("DispatcherServlet�� �غ���� �ʾҽ��ϴ�");
		return this.getContext().getBean(beanType);
	}
	
	/**
	 * ModelAndView�� ��ȯ�ϴ� �޼ҵ�
	 */
	public ModelAndView getModelAndView() {
		return this.dispatcherServlet.getModelAndView();
	}

	/**
	 * ��ü������ assertThat �׽�Ʈ ����
	 */
	public AfterRunService assertModel(String name, Object value) {
		assertThat(this.getModelAndView().getModel().get(name), is(value));
		return this;
	}

	/**
	 * ��ü������ assertThat �׽�Ʈ ����
	 */
	public AfterRunService assertViewName(String viewName) {
		assertThat(this.getModelAndView().getViewName(), is(viewName));
		return this;
	}

	/**
	 * ��ü������ assertThat �׽�Ʈ ����
	 */
	public String getContentAsString() throws UnsupportedEncodingException {
		return this.response.getContentAsString();
	}
	
	/**
	 * �׽�Ʈ ���� �Ŀ� �ڵ����� Context �ڿ��� ������
	 */
	@After
	public void closeServletContext() {
		if (this.dispatcherServlet != null) {
			((ConfigurableApplicationContext)dispatcherServlet.getWebApplicationContext()).close();
		}
	}
}

