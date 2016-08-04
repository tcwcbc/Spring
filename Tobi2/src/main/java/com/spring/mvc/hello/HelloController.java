package com.spring.mvc.hello;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 커스텀 컨트롤러.
 * 꼭 Springdml Controller 인터페이스를 상속받아야함.
 * 그렇게 안하면 기본으로 설정되는 ControllerHandlerAdapter가 못 붙음.
 * @author 최병철
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
