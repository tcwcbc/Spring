package com.spring.mvc.hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 서버환경에서 DispatcherServlet이 ServletContext를 만들때 해당 설정 값들을 임의대로 조정하여 독립적인 설정을 가질 수 있게 함.
 * 
 * 굳이 .jsp와 같은 뷰를 사용하지 않고 중간에 ModelAndView객체를 가로채서 반환해 주기 때문에 편리함.
 * 
 * @author 최병철
 *
 */
public class ConfigurableDispatcherServlet extends DispatcherServlet { 

	//설정 방법을 확장하여 경로와 클래스, 두 가지 오브젝트를 사용하게 함
	protected Class<?>[] classes;
    private String[] locations;
    
    //컨트롤러가 DispatcherServlet에 반환하는 ModelAndView 객체를 저장하여 빼내올 수 있게 함.
    private ModelAndView modelAndView;
    
    public ConfigurableDispatcherServlet(String[] locations) {
    	this.locations = locations;
	}

	public ConfigurableDispatcherServlet(Class<?> ...classes) { 
        this.classes = classes; 
    }
    
    public void setLocations(String ...locations) {
		this.locations = locations;
	}
    
    /**
     * 주어진 클래스로 부터 상대 위치의 클래스패스에 있는 설정파일 지정
     * @param clazz				주어지는 클래스
     * @param relativeLocations	상대위치 경로
     */
    public void setRelativeLocations(Class clazz, String ...relativeLocations) {
    	String[] locations = new String[relativeLocations.length];
    	String currentPath = ClassUtils.classPackageAsResourcePath(clazz) + "/";
    	for(int i=0; i<relativeLocations.length; i++) {
    		locations[i] = currentPath + relativeLocations[i]; 
    	}
    	this.setLocations(locations);
    }

	public void setClasses(Class<?> ...classes) {
		this.classes = classes;
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		modelAndView = null;
		super.service(req, res);
	}
	
	/**
	 * 	DispatcherServlet의 서블릿 컨텍스트를 생성하는 메소드를 오바라이드해서 테스트용 메타정보를 이용한 서블릿 컨텍스트 생성.
	 */
	protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) { 
        AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() { 
            protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) 
                    throws BeansException, IOException {
            	if (locations != null) {
	            	XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(beanFactory);
	            	xmlReader.loadBeanDefinitions(locations);
            	}
                AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory); 
                if (classes != null) {
	                reader.register(classes); 
            	}
            } 
        };

        wac.setServletContext(getServletContext()); 
        wac.setServletConfig(getServletConfig()); 
        wac.refresh(); 
        
        return wac; 
    }

	/**
	 * 뷰를 실행하는 과정에서 컨트롤러가 반환하는 ModelAndView 객체를 가로채는 메소드.
	 * 이렇게 가로챈 ModelAndView 객체를 가지고 테스트 검증을 함.
	 */
	protected void render(ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//스틸!!
		this.modelAndView = mv;
		super.render(mv, request, response);
	}

	public ModelAndView getModelAndView() {
		return modelAndView;
	}
}

