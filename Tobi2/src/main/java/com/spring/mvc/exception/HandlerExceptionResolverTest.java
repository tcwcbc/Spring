package com.spring.mvc.exception;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * 예외처리 테스트
 * 보통 예외가 발생하면 Service->Controller->Servlet 순으로 예외를 던지는데 이때 아무런 설정을 하지 않을 경우 505(내부에러)에러코드가 나온다.
 * web.xml에 <error-page>를 통해 예외발생시 보여줄 페이지를 지정할 수 있다.
 * 
 * 예외가 발생했을 때 이를 처리하는 HandlerExceptionResolver 4가지
 *  1. Annotation	: @ExceptionHandler 애노테이션으로 처리할 예외클래스를 지정한다
 *  					보통의 @RequestMapping과 구현이 비슷
 *  2. ResponseStatus : 예외클래스를 정의할 때 @ResponseStatus(value=,reason=)의ㅣ 형태로 예외에 대하여 설명을 한다.
 *  3. Default 		: 스프링MVC 내부에서 동작, 다른 예외 리졸버를 빈으로 등록한다면 이 예외 리졸버도 등록하는 것이 좋다.
 *  4. SimpleMapping	: 뷰 리졸버의 contentnegotiate리졸버와 유사, afterCompletio()로 관리자에게 알림 기능.
 * @author 최병철
 *
 */
public class HandlerExceptionResolverTest extends AbstractDispatcherServletTest  {
	@Test
	public void annotationMethod() throws ServletException, IOException {
		setClasses(HelloCon.class);
		runService("/hello");
		assertViewName("dataexception");
		System.out.println(getModelAndView().getModel().get("msg"));
	}
	@RequestMapping
	static class HelloCon {
		@RequestMapping("/hello")
		public void hello() {
			if (1==1) throw new DataRetrievalFailureException("hi");
		}
		
		/**
		 * 예외처리핸들러
		 * @param ex	애노테이션에서 지정한 DataAccessException
		 * @return		ModelAndView 객체로 예외에 대한 정보 리턴
		 */
		@ExceptionHandler(DataAccessException.class)
		public ModelAndView dataAccessExceptionHandler(DataAccessException ex) {
			return new ModelAndView("dataexception").addObject("msg", ex.getMessage());
		}
	}
	
	@Test
	public void responseStatus() throws ServletException, IOException {
		setClasses(HelloCon2.class);
		runService("/hello");
		System.out.println(response.getStatus());
		System.out.println(response.getErrorMessage());
	}
	@RequestMapping
	static class HelloCon2 {
		@RequestMapping("/hello")
		public void hello() {
			if (1==1) throw new NotInServiceException();
		}
	}
	
	/**
	 * @ResponseStatus 애노테이션을 통해 예외코드와 설명을 부가적으로 기술
	 * @author 최병철
	 *
	 */
	@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="서비스 긴급 점검중")
	static class NotInServiceException extends RuntimeException {
	}
	
}
