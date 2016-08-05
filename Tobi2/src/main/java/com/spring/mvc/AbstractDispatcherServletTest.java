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
 * DispatcherServlet 테스트를 위한 기반 클래스
 * 테스트를 위해 이 클래스를 상속받는다.
 * @author 최병철
 *
 */
public abstract class AbstractDispatcherServletTest implements AfterRunService {
	
	//목 오브젝트, 상속받은 자식클래스에서 접근이 가능하도록 protected
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	protected MockServletConfig config = new MockServletConfig("spring");
	protected MockHttpSession session;
	protected ConfigurableDispatcherServlet dispatcherServlet;
	
	//설정을 위해 클래스, 절대경로, 상대경로를 이용
	private Class<?>[] classes;
	private String[] locations;
	private String[] relativeLocations;
	private String servletPath;
	
	/**
	 * 절대경로 지정
	 * @param locations	절대경로
	 * @return			빌더패턴
	 */
	public AbstractDispatcherServletTest setLocations(String ...locations) {
		this.locations = locations;
		return this;
	}
	
	/**
	 * 상대경로 지정
	 * @param relativeLocations	상대경로
	 * @return					빌더패턴
	 */
	public AbstractDispatcherServletTest setRelativeLocations(String ...relativeLocations) {
		this.relativeLocations = relativeLocations;
		return this;
	}
	
	/**
	 * 빈 등록
	 * @param classes	등록할 빈
	 * @return			빌더패턴
	 */
	public AbstractDispatcherServletTest setClasses(Class<?> ...classes) {
		this.classes = classes;
		return this;
	}
	
	/**
	 * 서블릿 컨텍스트 경로 지정
	 * @param servletPath	서블릿 컨텍스트 경로
	 * @return				빌더패턴
	 */
	public AbstractDispatcherServletTest setServletPath(String servletPath) {
		if (this.request == null)
			this.servletPath = servletPath;
		else
			this.request.setServletPath(servletPath);
		return this;
	}

	/**
	 * Request 초기화
	 * @param requestUri	요청경로
	 * @param method		요청 메소드 이름
	 * @return				빌더패턴
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri, String method) {
		this.request = new MockHttpServletRequest(method, requestUri);
		this.response = new MockHttpServletResponse();
		if (this.servletPath != null) this.setServletPath(this.servletPath);
		return this;
	}
	
	/**
	 * Request 초기화
	 * @param requestUri	요청경로
	 * @param method		요청 메소드(Enum)
	 * @return				빌더패턴
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri, RequestMethod method) {
		return this.initRequest(requestUri, method.toString());
	}
	
	/**
	 * Request 초기화
	 * @param requestUri	요청경로
	 * @return				빌더패턴
	 */
	public AbstractDispatcherServletTest initRequest(String requestUri) {
		initRequest(requestUri, RequestMethod.GET);
		return this;
	}
	
	/**
	 * 파라미터 추가
	 * @param name		파라미터 명
	 * @param value		파라미터 값
	 * @return			빌더패턴
	 */
	public AbstractDispatcherServletTest addParameter(String name, String value) {
		if (this.request == null) 
			throw new IllegalStateException("request가 초기화되지 않았습니다.");
		this.request.addParameter(name, value);
		return this;
	}
	
	/**
	 * DispatcherServlet 빌드
	 * @return					빌더패턴
	 * @throws ServletException	IllegalStateException으로 포장
	 */
	public AbstractDispatcherServletTest buildDispatcherServlet() throws ServletException {
		if (this.classes == null && this.locations == null && this.relativeLocations == null) 
			throw new IllegalStateException("classes와 locations 중 하나는 설정해야 합니다");
		this.dispatcherServlet = createDispatcherServlet();
		this.dispatcherServlet.setClasses(this.classes);
		this.dispatcherServlet.setLocations(this.locations);
		if (this.relativeLocations != null)
			this.dispatcherServlet.setRelativeLocations(getClass(), this.relativeLocations);
		this.dispatcherServlet.init(this.config);
		
		return this;
	}
	
	/**
	 * DispatcherServlet 설정파일 생성
	 * @return	ConfigurableDispatcherServlet
	 */
	protected ConfigurableDispatcherServlet createDispatcherServlet() {
		return new ConfigurableDispatcherServlet();
	}

	/**
	 * 서비스 시작(Context 생성)
	 * @return	AfterRunService 인터페이스를 구현한 AbstractDispatcherServletTest클래스 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService() throws ServletException, IOException {
		if (this.dispatcherServlet == null) buildDispatcherServlet(); 
		if (this.request == null) 
			throw new IllegalStateException("request가 준비되지 않았습니다");
		this.dispatcherServlet.service(this.request, this.response);
		return this;
	}
	
	/**
	 * 서비스 시작(Context 생성)
	 * @param requestUri	요청 Uri 
	 * @return				AfterRunService 인터페이스를 구현한 AbstractDispatcherServletTest클래스 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService(String requestUri) throws ServletException, IOException {
		initRequest(requestUri);
		runService();
		return this;
	}
	
	/**
	 * 서비스 시작(Context 생성)
	 * @param requestUri	요청 Uri 
	 * @param method		요청 메소드 이름
	 * @return				AfterRunService 인터페이스를 구현한 AbstractDispatcherServletTest클래스 
	 * @throws ServletException
	 * @throws IOException
	 */
	public AfterRunService runService(String requestUri, String method) throws ServletException, IOException {
		initRequest(requestUri, method);
		runService();
		return this;
	}
	
	/**
	 * 컨텍스트 자체를 반환하는 메소드
	 */
	public ConfigurableWebApplicationContext getContext() {
		if (this.dispatcherServlet == null) 
			throw new IllegalStateException("DispatcherServlet이 준비되지 않았습니다");
		return (ConfigurableWebApplicationContext)this.dispatcherServlet.getWebApplicationContext();
	}
	
	/**
	 * DL을 위한 메소드
	 */
	public <T> T getBean(Class<T> beanType) {
		if (this.dispatcherServlet == null) 
			throw new IllegalStateException("DispatcherServlet이 준비되지 않았습니다");
		return this.getContext().getBean(beanType);
	}
	
	/**
	 * ModelAndView를 반환하는 메소드
	 */
	public ModelAndView getModelAndView() {
		return this.dispatcherServlet.getModelAndView();
	}

	/**
	 * 자체적으로 assertThat 테스트 수행
	 */
	public AfterRunService assertModel(String name, Object value) {
		assertThat(this.getModelAndView().getModel().get(name), is(value));
		return this;
	}

	/**
	 * 자체적으로 assertThat 테스트 수행
	 */
	public AfterRunService assertViewName(String viewName) {
		assertThat(this.getModelAndView().getViewName(), is(viewName));
		return this;
	}

	/**
	 * 자체적으로 assertThat 테스트 수행
	 */
	public String getContentAsString() throws UnsupportedEncodingException {
		return this.response.getContentAsString();
	}
	
	/**
	 * 테스트 종료 후에 자동으로 Context 자원을 해제함
	 */
	@After
	public void closeServletContext() {
		if (this.dispatcherServlet != null) {
			((ConfigurableApplicationContext)dispatcherServlet.getWebApplicationContext()).close();
		}
	}
}

