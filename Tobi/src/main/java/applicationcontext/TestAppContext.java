package applicationcontext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

import user.dao.UserDao;
import user.service.DummyMailSender;
import user.service.UserService;
import user.service.UserServiceTest.TestUserService;

/**
 * 테스트용 빈들을 위한 설정파일로 분리
 * @author 최병철
 *
 */
@Configuration
public class TestAppContext {
	//@Component를 통해 빈으로 등록할 오브젝트를 등록하고 @Autowired로 받아옴.
	//ApplicationContext에서 빈으로 등록 안해도 됨
	@Autowired
	UserDao userDao;
	
	//클래스에서 빈을 생성할 때는 접근제한자 맞춰야함
	@Bean
	public UserService testUserService(){
		TestUserService testUserServiceImpl = new TestUserService();
		testUserServiceImpl.setMailSender(mailSender());
		testUserServiceImpl.setUserDao(this.userDao);
		return testUserServiceImpl;
	}
	
	@Bean
	public MailSender mailSender(){
		return new DummyMailSender();
	}
}
