package user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
@DirtiesContext
public class UserServiceTest {
	@Autowired
	UserService userService;
	
//	@Autowired
//	UserLevelUpgradePolicyEx userPolicy;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource;
	
	List<User> users;
	
	@Before
	public void setUp(){
		users = Arrays.asList(
				new User("Test1","테스트1","p1",Level.BASIC, userService.MIN_LOGCOUNT_FOR_SILVER-1, 0,"Test1@test.com"),
				new User("Test2","테스트2","p2",Level.BASIC, userService.MIN_LOGCOUNT_FOR_SILVER, 0,"Test2@test.com"),
				new User("Test3","테스트3","p3",Level.SILVER, 60, userService.MIN_RECOMMANDCOUNT_FOR_GOLD-1,"Test3@test.com"),
				new User("Test4","테스트4","p4",Level.SILVER, 60, userService.MIN_RECOMMANDCOUNT_FOR_GOLD,"Test4@test.com"),
				new User("Test5","테스트5","p5",Level.GOLD, 100, Integer.MAX_VALUE,"Test5@test.com")
				);
	}
	
	@Test
	@DirtiesContext
	public void upgradeLevels() throws SQLException{
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);
		
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
		
		List<String> requset = mockMailSender.getRequsets();
		assertThat(requset.size(), is(2));
		assertThat(requset.get(0), is(users.get(1).getEmail()));
		assertThat(requset.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkLevel(User user, Boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if(upgraded){
			assertThat(userUpdate.getLevel(), is(user.getLevel().getNextLevel()));
		}else{
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
		
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
	}
	
	/**
	 * 일괄적으로 회원들의 등급을 업그레이드
	 * @throws Exception SQLException
	 */
	@Test
	public void upgradeAllOrNothing() throws Exception {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(this.transactionManager);
		testUserService.setMailSender(this.mailSender);
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try{
			testUserService.upgradeLevels();
//			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e){}
		
		checkLevel(users.get(1), false);
	}
	
	/**
	 * 트렌잭션 문제를 일으키기위한 UserService 클래스
	 * @author 최병철
	 *
	 */
	static class TestUserService extends UserService{
		private String id;
		
		private TestUserService(String id){
			this.id = id;
		}
		
		public void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException{
		
	}
	
	/**
	 * 대상오브젝트(UserService)가 의존 오브젝트와 어떤 통신을 하는지 검증하기 위한 Mock클래스
	 * 목적지 주소를 리스트에 저장하여 반환
	 * @author 최병철
	 *
	 */
	static class MockMailSender implements MailSender{
		
		private List<String> requsts = new ArrayList<String>();
		
		public List<String> getRequsets(){
			return requsts;
		}

		@Override
		public void send(SimpleMailMessage arg0) throws MailException {
			// TODO Auto-generated method stub
			requsts.add(arg0.getTo()[0]);
			
		}

		@Override
		public void send(SimpleMailMessage[] arg0) throws MailException {
			// TODO Auto-generated method stub
			
		}
		
	}
}
