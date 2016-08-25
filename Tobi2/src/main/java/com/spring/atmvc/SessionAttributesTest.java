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
 * @SessionAttributes ������ �̸��� ������ ������Ʈ�� ���ϵǾ� �������� ������ ��
 * 						������ �����ϴ� ���� ��ü�� �����ϰ�, �Ŀ� �ش� Ÿ���� ����ü�� @ModelAttribute��
 * 						������ �� �ش� ���ǿ� �ִ��� Ȯ�� �Ŀ� �����´�.
 * 
 * 	GET��û�� POST��û�� �Ͼ�� ���� �۾��Ӹ� �ƴ϶� ����ϴ� ��쿡�� ���Ƿ� �����ο�����Ʈ�� �����
 * 	���ǿ� ������ �Ŀ� ���� �����شٸ�, �Ŀ� �߸��� ���� �Է��Ͽ� �ٽ� �õ��ϱ⸦ �䱸�� �� ������
 * 	�Է��߾��� ���� �״�� �������� ������ �� �־ @SessionAttributes �� ����Ѵ�.
 * @author �ֺ�ö
 *
 */
public class SessionAttributesTest extends AbstractDispatcherServletTest {
	@Test
	public void sessionAttr() throws ServletException, IOException {
		setClasses(UserController.class);
		//GET��û(����Ʈ)
		initRequest("/user/edit").addParameter("id", "1");
		runService();

		HttpSession session = request.getSession();
		//�𵨷� ���ϵ� ��ü�� ���ǿ� ����� ��ü�� ������ �˻�
		assertThat(session.getAttribute("user"), is(getModelAndView().getModel().get("user")));

		//POST��û(email ������Ƽ ����)
		initRequest("/user/edit", "POST").addParameter("id", "1").addParameter("name", "Spring2");
		//���� ���ǻ��� ����
		request.setSession(session);
		runService();
		//�ι�° ��û(POST)���� ������ ���Ͽ� ���� ������ ù��° ��û�� ����� ������ �����Ǵ��� �׽�Ʈ
		assertThat(((User) getModelAndView().getModel().get("user")).getEmail(), is("mail@spring.com"));
		
		//SesstionStatus.setComplete()�� ������ �����Ǿ����� Ȯ��
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
			//���� �����
			sessionStatus.setComplete();
		}
	}

	/**
	 * �׽�Ʈ�� ������ ������Ʈ
	 * @author �ֺ�ö
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
