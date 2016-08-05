package com.spring.mvc;

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
 * ����ȯ�濡�� DispatcherServlet�� ServletContext�� ���鶧 �ش� ���� ������ ���Ǵ�� �����Ͽ� �������� ������ ���� �� �ְ� ��.
 * 
 * ���� .jsp�� ���� �並 ������� �ʰ� �߰��� ModelAndView��ü�� ����ä�� ��ȯ�� �ֱ� ������ ������.
 * 
 * @author �ֺ�ö
 *
 */
public class ConfigurableDispatcherServlet extends DispatcherServlet { 

	//���� ����� Ȯ���Ͽ� ��ο� Ŭ����, �� ���� ������Ʈ�� ����ϰ� ��
	protected Class<?>[] classes;
    private String[] locations;
    
    //��Ʈ�ѷ��� DispatcherServlet�� ��ȯ�ϴ� ModelAndView ��ü�� �����Ͽ� ������ �� �ְ� ��.
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
     * �־��� Ŭ������ ���� ��� ��ġ�� Ŭ�����н��� �ִ� �������� ����
     * @param clazz				�־����� Ŭ����
     * @param relativeLocations	�����ġ ���
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
	 * 	DispatcherServlet�� ������ ���ؽ�Ʈ�� �����ϴ� �޼ҵ带 ���ٶ��̵��ؼ� �׽�Ʈ�� ��Ÿ������ �̿��� ������ ���ؽ�Ʈ ����.
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
	 * �並 �����ϴ� �������� ��Ʈ�ѷ��� ��ȯ�ϴ� ModelAndView ��ü�� ����ä�� �޼ҵ�.
	 * �̷��� ����æ ModelAndView ��ü�� ������ �׽�Ʈ ������ ��.
	 */
	protected void render(ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//��ƿ!!
		this.modelAndView = mv;
		super.render(mv, request, response);
	}

	public ModelAndView getModelAndView() {
		return modelAndView;
	}
}
