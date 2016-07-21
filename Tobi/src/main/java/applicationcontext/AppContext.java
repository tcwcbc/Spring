package applicationcontext;

import javax.sql.DataSource;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
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
import user.service.UserService;
import user.service.UserServiceImpl;
import user.service.UserServiceTest.TestUserService;

/**
 * Spring 3.0 ->  Spring 3.1 
 * context.xml -> context.class+@
 * @author 최병철
 *
 * xml의 빈들을 클래스에서 정의 할 때는 리턴타입을 고려하여 가능한 인터페이스 타입으로 하고
 * 메소드 이름은 빈 아이디로 하면 좋다.
 * @Autowired는 변수타입으로 매핑하고 @Resource는 변수 명으로 매핑함.
 *
 * xml로 된 설정파일과 클래스 파일로 된 설정파일을 함께 사용할 수 있다.
 * xml의 빈을 클래스로 가져오려면 @Autowired 로 주입받아서 사용.
 * 클래스에 정의한 빈은 xml에서 property로 참조하면 된다.
 * 
 * 빈 내부에서는 인터페이스의 구현체에 의존적인 setter를 통하여 세팅하므로
 * 리턴타입은 인터페이스를 통하고 빈 내부에서는 구현체 타입의 로컬변수로 받음.
 * 
 * 테스트용 빈들은 TestAppContext로 분리해냄
 */
@Configuration
@EnableTransactionManagement
//@ImportResource("/test-applicationContext.xml")
//@Component를 통해 빈으로 자동 등록된 오브젝트들을 스캔(범위)
@ComponentScan("user")
public class AppContext {
	
//	1. <context:annotation-config/> 삭제
//	- 빈 생성 후처리기 등록을 위한 태그(@PostConstruct)
	//2. dataSource빈 변환
	@Bean
	public DataSource dataSource(){
		//DataSource 인터페이스는 커넥션 기능박에 없음.
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(Driver.class);
		dataSource.setUrl("jdbc:mysql://Localhost/users?characterEncoding=UTF-8");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		return dataSource;
	}
	
	//3. 트랜잭션 매니저 빈 변경
	@Bean
	public PlatformTransactionManager transactionManager(){
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
//	
//	@Autowired 
//	SqlService sqlService;
	//4. userDao 빈 변경
//	@Bean
//	public UserDao userDao(){
//		UserDaoJdbc daoJdbc = new UserDaoJdbc();
//		//UserDaoJdbc의 세터에 @AutoWired로 인해 자동으로 빈을 검색하여 주입
////		daoJdbc.setDataSource(dataSource());
//		//UserDaoJdbc의 인스턴스 변수 sqlService에 @AutoWired
////		daoJdbc.setSqlService(this.sqlService);
//		return daoJdbc;
//	}
	
	@Bean 
	public SqlRegistry sqlRegistry(){
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(embeddedDatabase());
		return embeddedDbSqlRegistry;
	}
	
	@Bean
	public Unmarshaller unmarshaller(){
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath("user.sqlservice.jaxb");
		return jaxb2Marshaller;
	}
	

	//5. userService 빈 변경
//	@Bean
//	public UserService userService(){
//		UserServiceImpl serviceImpl = new UserServiceImpl();
//		serviceImpl.setMailSender(mailSender());
//		serviceImpl.setUserDao(this.userDao);
//		return serviceImpl;
//	}
	

	
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(HSQL)
				.addScript("classpath:embeddeddb/schema.sql")
				.build();
	}
	
	@Bean 
	public SqlService sqlService(){
		OxmSqlService oxmSqlService = new OxmSqlService();
		oxmSqlService.setUnmarshaller(unmarshaller());
		oxmSqlService.setSqlRegistry(sqlRegistry());
		return oxmSqlService;
	}
}
