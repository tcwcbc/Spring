package user.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import user.domain.Level;
import user.domain.User;

public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		this.user = new User();
	}
	
	@Test
	public void upgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.getNextLevel()==null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.getNextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.getNextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
}
