package user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import user.domain.Level;
import user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
@DirtiesContext
public class UserDaoTest {
	@Autowired
	ApplicationContext context;
	
	@Autowired
	 UserDao dao; 
	
	@Autowired
	DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		this.dao = this.context.getBean("userDao", UserDao.class);
		
		this.user1 = new User("gyumee", "이름1", "springno1",Level.BASIC, 1, 0,"Test1@test.com");
		this.user2 = new User("leegw700", "이름2", "springno2", Level.SILVER, 55, 10,"Test1@test.com");
		this.user3 = new User("bumjin", "이름3", "springno3", Level.GOLD, 100, 40,"Test1@test.com");

	}
	
	/**
	 * DB에 저장이 잘 되었는지 테스트
	 * @throws SQLException
	 */
	@Test 
	public void andAndGet() throws SQLException {		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
		
	}

	/**
	 * DB가 비어있을 경우
	 * @throws SQLException
	 */
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}

	/**
	 * 몇개인지확인
	 * @throws SQLException
	 */
	@Test
	public void count() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
				
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	

	/**
	 * DB에 모든 값 가져오기
	 * @throws SQLException
	 */
	@Test
	public void getAll() throws SQLException {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1); // Id: gyumee
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2); // Id: leegw700
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));  
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3); // Id: bumjin
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));  
		checkSameUser(user1, users3.get(1));  
		checkSameUser(user2, users3.get(2));  
	}
	
	/**
	 * 수정 테스트
	 */
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("최병철");
		user1.setPassword("tcwcbc");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommand(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1update, user1);
		User user2update = dao.get(user2.getId());
		checkSameUser(user2update, user2);
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommand(), is(user2.getRecommand()));
	}
	
	/**
	 * 중복체크
	 */
	@Test(expected=DataAccessException.class)
	public void duplicateKey(){
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
//	@Test
//	public void sqlExceptionTranslate() {
//		dao.deleteAll();
//		
//		try {
//			dao.add(user1);
//			dao.add(user1);
//		}
//		catch(DuplicateKeyException ex) {
//			SQLException sqlEx = (SQLException)ex.getCause();
//			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);			
//			DataAccessException transEx = set.translate(null, null, sqlEx);
//			assertThat(transEx, is(DuplicateKeyException.class));
//		}
//	}

}
