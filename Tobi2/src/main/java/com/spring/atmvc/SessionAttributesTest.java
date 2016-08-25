package com.spring.atmvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * @SessionAttributes 지정한 이름의 도메인 오브젝트가 리턴되어 서블릿으로 전해질 때
 * 						세션을 저장하는 곳에 객체를 저장하고, 후에 해당 타입의 ㄱ객체를 @ModelAttribute로
 * 						가져올 때 해당 세션에 있는지 확인 후에 가져온다.
 * 
 * 	GET요청과 POST요청이 일어나는 수정 작업뿐만 아니라 등록하는 경우에도 임의로 도메인오브젝트를 만들어
 * 	세션에 저장한 후에 폼을 보여준다면, 후에 잘못된 값을 입력하여 다시 시도하기를 요구할 때 기존의
 * 	입력했었던 값을 그대로 유지시켜 보여줄 수 있어서 @SessionAttributes 를 사용한다.
 * @author 최병철
 *
 */
public class SessionAttributesTest extends AbstractDispatcherServletTest {
	@Test
	public void sessionAttr() throws ServletException, IOException {
		setClasses(UserController.class);
		//GET요청(디폴트)
		initRequest("/user/edit").addParameter("id", "1");
		runService();

		HttpSession session = request.getSession();
		//모델로 리턴된 객체와 세션에 저장된 객체가 같은지 검사
		assertThat(session.getAttribute("user"), is(getModelAndView().getModel().get("user")));

		//POST요청(email 프로퍼티 없음)
		initRequest("/user/edit", "POST").addParameter("id", "1").addParameter("name", "Spring2");
		//앞의 세션상태 유지
		request.setSession(session);
		runService();
		//두번째 요청(POST)에서 누락된 메일에 대한 정보가 첫번째 요청에 저장된 값으로 유지되는지 테스트
		assertThat(((User) getModelAndView().getModel().get("user")).getEmail(), is("mail@spring.com"));
		
		//SesstionStatus.setComplete()로 세션이 해제되었는지 확인
		assertThat(session.getAttribute("user"), is(nullValue()));
	}
	


	@Controller
	@SessionAttributes("user")
	static class UserController {
		@RequestMapping(value = "/user/edit", method = RequestMethod.GET)
		public User form(@RequestParam int id) {
			return new User(1, "Spring", "mail@spring.com");
		}

		@RequestMapping(value = "/user/edit", method = RequestMethod.POST)
		public void submit(@ModelAttribute User user, SessionStatus sessionStatus) {
			//세션 지우기
			sessionStatus.setComplete();
		}
	}

	/**
	 * 테스트용 도메인 오브젝트
	 * @author 최병철
	 *
	 */
	static class User {
		int id;
		String name;
		String email;

		public User(int id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public User() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String toString() {
			return "User [email=" + email + ", id=" + id + ", name=" + name + "]";
		}
	}
}
