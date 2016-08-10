package com.spring.mvc.flashmap;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * FlashMap은 FlashAttribute를 저장하는 맵.
 * FlashAttribute는 세션처럼 하나의 요청에서 다음 요청으로 값을 넘겨주는 속성(Android의 intent)
 * 세션과 비슷하지만 FlashAttribute는 한번 사용된 값은 사용된 후에 삭제된다.
 * 보통 Post/Redirect/Get 패턴(요청완료 메시지가 갱신될때마다 출력되지 않도록 Redirect)
 * 
 * @author 최병철
 *
 */
public class FlashMapTest extends AbstractDispatcherServletTest {
	@Test
	public void flashMapTest() throws ServletException, IOException {
		setClasses(PostController.class, RedirectController.class, OtherController.class);
		runService("/flash");

		assertThat(this.getModelAndView().getViewName(), is("redirect:/redirect"));
		
		//FlashMap을 저장하기 위한 세션객체
		HttpSession sessionSaved = request.getSession();
		
		// 1st request
		initRequest("/redirect");
		request.setSession(sessionSaved);
		runService();
		
		sessionSaved = request.getSession();
		
		// 2nd request
		initRequest("/redirect");
		request.setSession(sessionSaved);
		try {
			runService();
			fail();
		}
		catch(Exception e) {
			assertThat(e.getCause(), is(NullPointerException.class));
		}
	}
	
	@Test
	public void flashMapWithRequestPath() throws ServletException, IOException {
		setClasses(PostController.class, RedirectController.class, OtherController.class);
		runService("/flash");
		
		HttpSession sessionSaved = request.getSession();
		
		// /other
		initRequest("/other");
		request.setSession(sessionSaved);
		try {
			runService();
			fail();
		}
		catch(Exception e) {
			assertThat(e.getCause(), is(NullPointerException.class));
		}
		
		sessionSaved = request.getSession();
		
		// /redirect
		initRequest("/redirect");
		request.setSession(sessionSaved);
		runService();
	}
	
	/**
	 * 초기 PostController
	 * @author 최병철
	 *
	 */
	@RequestMapping("/flash")
	static class PostController implements Controller {
		@Override
		public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			//request객체로부터 FlashMap을 가져옴
			FlashMap fm = RequestContextUtils.getOutputFlashMap(request);
			fm.put("message", "Saved");
			fm.setTargetRequestPath("/redirect");
			
			return new ModelAndView("redirect:/redirect");
		}
	}
	
	/**
	 * redirection을 받아 처리하는 컨트롤러
	 * @author 최병철
	 *
	 */
	@RequestMapping("/redirect")
	static class RedirectController implements Controller {
		@Override
		public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			//FlashMap을 가져와 저장된 값을 가져옴
			assertThat((String)RequestContextUtils.getInputFlashMap(request).get("message"), is("Saved"));
			
			return new ModelAndView("next");
		}
	}
	
	/**
	 * 별도의 Controller
	 * @author 최병철
	 *
	 */
	@RequestMapping("/other")
	static class OtherController implements Controller {
		@Override
		public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			assertThat((String)RequestContextUtils.getInputFlashMap(request).get("message"), is("Saved"));
			
			return new ModelAndView("next");
		}
	}
	
}
