package com.spring.mvc.hello;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Ŀ���� ��Ʈ�ѷ�.
 * �� Springdml Controller �������̽��� ��ӹ޾ƾ���.
 * �׷��� ���ϸ� �⺻���� �����Ǵ� ControllerHandlerAdapter�� �� ����.
 * @author �ֺ�ö
 *
 */
public class HelloController implements Controller{

	@Autowired HelloSpring helloSpring;
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
		
		String message = this.helloSpring.say(name);
		
		Map<String, String> model = new HashMap<String, String>();
		
		model.put("message", message);
		
		return new ModelAndView("home", model);
	}

}
