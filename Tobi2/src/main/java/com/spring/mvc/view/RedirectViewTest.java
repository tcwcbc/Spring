package com.spring.mvc.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * RedirectView는 httpServletResponse의 sendRedirect()를 호출.
 * 실제 뷰가 생성되는 것이 아니라 URL뒤에 파라미터만 추가되어 리다이렉트 된다.
 * 
 * ModelAndView에 RedirectView 객체를 생성하여 넣어주거나, ViewResolver가 인식하게 redirect:뷰이름 과같은 형식으로 리턴
 * @author 최병철
 *
 */
public class RedirectViewTest extends AbstractDispatcherServletTest {
	@Test
	public void redirectView() throws ServletException, IOException {
		setClasses(HelloController.class);		
		runService("/hello");
		assertThat(this.response.getRedirectedUrl(), is("/main?name=Spring"));
	}
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
											//두번째 파라미터를 true로 하면 상대경로를 사용한다는 뜻. 서블릿 패스틑 포함X
			return new ModelAndView(new RedirectView("/main", true)).addObject("name", "Spring");
//			return new ModelAndView("redirect:/main").addObject("name", "Spring");
		}
	}
}
