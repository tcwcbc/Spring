package user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static user.service.UserServiceImpl.*;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import applicationcontext.AppContext;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;


/**
 * upgradeAllOrNothing 테스트에서 트랜잭션이 안먹힘.
 * 큰일 남. 이것때문에 readOnlyTransactionAttribute 테스트도 실패하는듯
 * 빨리해결해야함.
 * @author 최병철
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="/test-applicationContext.xml")
//설정파일을 xml -> java로 바꿈
@ActiveProfiles("test")
@ContextConfiguration(classes=AppContext.class)
//@DirtiesContext
//@Transactional	//해당 클래스 내의 일부분만 트랜잭션 적용을 하지 않으려면
				//@Transactional(propagation=Propagation.NEVER)

//롤백 안되게 설정
//@TransactionConfiguration(defaultRollback=false)
public class UserServiceTest {
	@Autowired 
	@Qualifier("userService")
	UserService userService;
//	
//	@Autowired
//	@Qualifier("userServiceImpl")
//	UserServiceImpl userServiceImpl;
//	
	@Autowired ApplicationContext context;
	
	//같은 타입 빈이 두 개 존재하면 필드명을 기준으로 결정 됨(X)
	//@Qualifier로 빈 id 지정해줘야함
	@Autowired 
	@Qualifier("testUserService")
	UserService testUserService;
	
//	@Autowired
//	UserLevelUpgradePolicyEx userPolicy;
	
	@Autowired MailSender mailSender;
	
	@Autowired PlatformTransactionManager transactionManager;
	
	@Autowired UserDao userDao;
	
//	@Autowired
//	DataSource dataSource;
	
	List<User> users;
	
	@Before
	public void setUp(){
		users = Arrays.asList(
				new User("Test1","테스트1","p1",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0,"Test1@test.com"),
				new User("Test2","테스트2","p2",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0,"Test2@test.com"),
				new User("Test3","테스트3","p3",Level.SILVER, 60, MIN_RECOMMANDCOUNT_FOR_GOLD-1,"Test3@test.com"),
				new User("Test4","테스트4","p4",Level.SILVER, 60, MIN_RECOMMANDCOUNT_FOR_GOLD,"Test4@test.com"),
				new User("Test5","테스트5","p5",Level.GOLD, 100, Integer.MAX_VALUE,"Test5@test.com")
				);
	}
	
	@Test
	public void upgradeLevels() throws Exception{
		//목 오브젝트를 사용한 클래스(내부의 static 클래스, 상속받은 클래스)
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//Mockito 프레임웍을 활용한 목 오브젝트 생성
		//getAll메소드를 
		MockUserDao mockUserDao = new MockUserDao(this.users);
//		when(mockUserDao.getAll()).thenReturn(this.users);
//		MockUserDao mockUserDao = new MockUserDao(this.users);
		
		userServiceImpl.setUserDao(mockUserDao);
		
		//실제 DB를 가지고 테스트
//		userDao.deleteAll();
//		for(User user : users) userDao.add(user);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "Test2", Level.SILVER);
		checkUserAndLevel(updated.get(1), "Test4", Level.GOLD);
		
//		checkLevel(users.get(0), false);
//		checkLevel(users.get(1), true);
//		checkLevel(users.get(2), false);
//		checkLevel(users.get(3), true);
//		checkLevel(users.get(4), false);
		
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
	
	private void checkUserAndLevel(User updated, String expectedId,Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
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
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	/**
	 * 일괄적으로 회원들의 등급을 업그레이드
	 * @throws Exception SQLException
	 */
	@Test
//	@DirtiesContext		//컨텍스트 무효화
	public void upgradeAllOrNothing() throws Exception {
//		TestUserService testUserService = new TestUserService(users.get(3).getId());
//		testUserService.setUserDao(this.userDao);
//		testUserService.setMailSender(this.mailSender);
//
//		//팩토리 빈 자체를 가져오기 위해서 '&' 필요
//		ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
//		
//		txProxyFactoryBean.setTarget(testUserService);
		
		//변경된 타겟으로 다시 객체 생성
//		UserService txUserService = (UserService) txProxyFactoryBean.getObject();
		
		//기존의 중복되었던 코드가 많던 UserServiceTx 프록시
//		UserServiceTx serviceTx = new UserServiceTx();
//		serviceTx.setUserService(testUserService);
		
//		//리플렉션을 활용한 TransactionHandler 다이나믹 프록시
//		TransactionHandler transactionHandler = new TransactionHandler();
//		transactionHandler.setTransactionManager(this.transactionManager);
//		transactionHandler.setPattern("upgradeLevels");
//		transactionHandler.setTarget(testUserService);
//		
//		UserService txUserService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(),
//				new Class[] {UserService.class}, transactionHandler);

		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try{
			testUserService.upgradeLevels();
//			serviceTx.upgradeLevels();
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e){}
		
		checkLevel(users.get(1), false);
	}
	@Test(expected=TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute(){
		testUserService.getAll();
	}
	
	/**
	 * 트랜잭션 전파 테스트
	 */
	@Test
	public void transactionSync(){
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//		txDefinition.setReadOnly(true);
		TransactionStatus status = transactionManager.getTransaction(txDefinition);
		try{
			
			userService.add(users.get(0));
			userService.add(users.get(1));
			assertThat(userDao.getCount(), is(2));
		}finally{
			transactionManager.rollback(status);
			assertThat(userDao.getCount(), is(0));
		}
	}
	
	/**
	 * 테스트 메소드에 트랜잭션을 적용
	 * 테스트용 트랜잭션은 끝나면 자동으로 롤백 됨
	 */
	@Test 
	@Transactional(readOnly=true)	//읽기 전용
	@Rollback(false) 	//원래 테스트 메소드에 @Transaction을 붙이 면 롤백되나 이 구문으로 롤백이 안됨, 메소드레벨만 적용 가능
						//클래스 레벨에 적용할때는 @TransactionConfiguration 사용
	public void trancationSync1(){
		userService.deleteAll();
	}
	
	/**
	 * 빈팩토리가 빈들을 관리하는 DefaultListableBeanFactory를 동적주입받아
	 * getBeanDefinitionNames() 로 확인하는 클래스
	 */
	@Autowired DefaultListableBeanFactory bf;
	@Test
	public void beans(){
		for(String n : bf.getBeanDefinitionNames()){
			System.out.println(n+"\t"+bf.getBean(n).getClass().getName());
		}
	}
	
	/**
	 * 트렌잭션 문제를 일으키기위한 UserService 클래스
	 * 프록시 적용을 시켜주기 위한 클래스 이름 변경 : TestUserServic->TestUserServiceImpl
	 * @author 최병철
	 *
	 */
	public static class TestUserService extends UserServiceImpl{
		//강제로 제외시켜주기 위한 users(3) 아이디 주입
		private String id="Test4";
		
//		private TestUserService(String id){
//			this.id = id;
//		}
		@Override
		public void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			for(User user : super.getAll()){
				super.update(user);
			}
			return null;
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
	
	
	/**
	 * UserServiceTest 클래스 내에서 사용자 레벨 업그레이드 테스트를 위한
	 * 목 클래스
	 * @author 최병철
	 *
	 */
	static class MockUserDao implements UserDao{
		
		private List<User> users;
		private List<User> updated = new ArrayList<User>();

		private MockUserDao(List<User> users){
			this.users = users;
		}
		
		private List<User> getUpdated(){
			return this.updated;
		}
		
		@Override
		public void add(User user) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public User get(String id) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			return this.users;
		}

		@Override
		public void deleteAll() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void setDataSource(DataSource dataSource) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void update(User user1) {
			// TODO Auto-generated method stub
			updated.add(user1);
		}
		
	}
}
