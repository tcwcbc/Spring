package user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
@DirtiesContext
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	List<User> users;
	
	@Before
	public void setUp(){
		users = Arrays.asList(
				new User("Test1","테스트1","p1",Level.BASIC, userService.MIN_LOGCOUNT_FOR_SILVER-1, 0),
				new User("Test2","테스트2","p2",Level.BASIC, userService.MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("Test3","테스트3","p3",Level.SILVER, 60, userService.MIN_RECOMMANDCOUNT_FOR_GOLD-1),
				new User("Test4","테스트4","p4",Level.SILVER, 60, userService.MIN_RECOMMANDCOUNT_FOR_GOLD),
				new User("Test5","테스트5","p5",Level.GOLD, 100, Integer.MAX_VALUE)
				);
	}
	
	@Test
	public void upgradeLevels(){
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
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
}
