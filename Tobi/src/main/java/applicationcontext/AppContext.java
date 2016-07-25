package applicationcontext;

import javax.management.RuntimeErrorException;
import javax.sql.DataSource;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.jdbc.Driver;

import user.dao.EmbeddedDbSqlRegistry;
import user.dao.OxmSqlService;
import user.dao.SqlRegistry;
import user.dao.SqlService;
import user.dao.UserDao;
import user.dao.UserDaoJdbc;
import user.service.DummyMailSender;
import user.service.SqlMapConfig;
import user.service.UserService;
import user.service.UserServiceImpl;
import user.service.UserServiceTest.TestUserService;

/**
 * Spring 3.0 -> Spring 3.1 context.xml -> context.class+@
 * 
 * @author 최병철
 *
 *         xml의 빈들을 클래스에서 정의 할 때는 리턴타입을 고려하여 가능한 인터페이스 타입으로 하고 메소드 이름은 빈 아이디로 하면
 *         좋다.
 * @Autowired는 변수타입으로 매핑하고 @Resource는 변수 명으로 매핑함.
 *
 *             xml로 된 설정파일과 클래스 파일로 된 설정파일을 함께 사용할 수 있다. xml의 빈을 클래스로
 *             가져오려면 @Autowired 로 주입받아서 사용. 클래스에 정의한 빈은 xml에서 property로 참조하면 된다.
 * 
 *             빈 내부에서는 인터페이스의 구현체에 의존적인 setter를 통하여 세팅하므로 리턴타입은 인터페이스를 통하고 빈
 *             내부에서는 구현체 타입의 로컬변수로 받음.
 * 
 *             테스트용 빈들은 TestAppContext로 분리해냄
 */
@Configuration
//애노테이션을 할용하여  외부 Context 파일임포트
@EnableSqlService
@EnableTransactionManagement
// @ImportResource("/test-applicationContext.xml")
// @Component를 통해 빈으로 자동 등록된 오브젝트들을 스캔(범위)
@ComponentScan("user")
// 서비스 계층의 컨텍스트를 분리하고 임포트로 다시 결합, 클라이언트에서 설정파일을 설정할 때
// 다시 classes에 추가하지 않아도 됨
// 따로 클래스파일로 분리했던 컨텍스트들을 내부 스태틱 클래스로 놓고 임포트
//@Import({ SqlServiceContext.class
//		/*
//		 * 내부 중첩 클래스로 정의한 설정 파일들은 임포트 선언을 안해줘도 된다. ,
//		 * AppContext.TestAppContext.class,
//		 * AppContext.ProductionAppContext.class
//		 */
//})
//프로퍼티 파일을 가져오는 애노테이션
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig{
	
	//치환자 : XML 설정에서 value 값을 치환하는 것과 유사
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}") String url;
	@Value("${db.username}") String username;
	@Value("${db.password}") String password;
	
	//빈 팩토리 후처리기 등록, 스태틱으로 정의해야함(빈 생성시 초기화 되어야 하기때문에)
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	
	//프로퍼티 파일을 관리하는 Enviroment객체를  DI 받아 DataSource 설정
//	@Autowired Environment ev;
	// 1. <context:annotation-config/> 삭제
	// - 빈 생성 후처리기 등록을 위한 태그(@PostConstruct)
	// 2. dataSource빈 변환
	@Bean
	public DataSource dataSource() {
		// DataSource 인터페이스는 커넥션 기능박에 없음.
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		//빈 팩토리 후처리기를 통해 치환자로 값을 주입하는 방식
		dataSource.setDriverClass(this.driverClass);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		
	//enviroment 객체로 프로퍼티 값을 가져오는 방식	
//		try{
//			dataSource.setDriverClass((Class<? extends java.sql.Driver>) 
//					Class.forName(ev.getProperty("db.driverClass")));
//		}catch(ClassNotFoundException e){
//			throw new RuntimeException(e);
//		}
//		dataSource.setUrl(ev.getProperty("db.url"));
//		dataSource.setUsername(ev.getProperty("db.username"));
//		dataSource.setPassword(ev.getProperty("db.password"));
		
		
//		dataSource.setDriverClass(Driver.class);
//		dataSource.setUrl("jdbc:mysql://Localhost/users?characterEncoding=UTF-8");
//		dataSource.setUsername("root");
//		dataSource.setPassword("1234");
		return dataSource;
	}

	// 3. 트랜잭션 매니저 빈 변경
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	//
	// @Autowired
	// SqlService sqlService;
	// 4. userDao 빈 변경
	// @Bean
	// public UserDao userDao(){
	// UserDaoJdbc daoJdbc = new UserDaoJdbc();
	// //UserDaoJdbc의 세터에 @AutoWired로 인해 자동으로 빈을 검색하여 주입
	//// daoJdbc.setDataSource(dataSource());
	// //UserDaoJdbc의 인스턴스 변수 sqlService에 @AutoWired
	//// daoJdbc.setSqlService(this.sqlService);
	// return daoJdbc;
	// }

	// 5. userService 빈 변경
	// @Bean
	// public UserService userService(){
	// UserServiceImpl serviceImpl = new UserServiceImpl();
	// serviceImpl.setMailSender(mailSender());
	// serviceImpl.setUserDao(this.userDao);
	// return serviceImpl;
	// }

	/**
	 * 테스트용 빈들을 위한 설정파일로 분리
	 * 
	 * @author 최병철
	 *
	 */
	@Configuration
	// 테스트 환경에서만 적용하기 위해 profile 단위로 묶음
	@Profile("test")
	public static class TestAppContext {
		// @Component를 통해 빈으로 등록할 오브젝트를 등록하고 @Autowired로 받아옴.
		// ApplicationContext에서 빈으로 등록 안해도 됨
		@Autowired
		UserDao userDao;

		// 클래스에서 빈을 생성할 때는 접근제한자 맞춰야함
		@Bean
		public UserService testUserService() {
			// TestUserService testUserServiceImpl = new TestUserService();
			// testUserServiceImpl.setMailSender(mailSender());
			// testUserServiceImpl.setUserDao(this.userDao);
			// return testUserServiceImpl;
			// 자동 와이어링으로 간략화
			return new TestUserService();
		}

		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}

	@Configuration
	// 테스트 환경에서만 적용하기 위해 profile 단위로 묶음
	@Profile("production")
	public class ProductionAppContext {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
			javaMailSenderImpl.setHost("localhost");
			return javaMailSenderImpl;
		}
	}

	//별도의 클래스를 생성하지 않고 AppContext가 직접 SqlMapConfig 인터페이스를 구현.
	//설정파일 자체도 하나의 빈이기 때문에 가능
	@Override
	public Resource getSqlMapConfig() {
		// TODO Auto-generated method stub
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}
}
