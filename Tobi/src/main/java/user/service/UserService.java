package user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

/**
 * 비지니스 로직을 담고있는 클래스
 * DAO는 순수하게 DB접근을 위한 행동을 하고
 * 비지니스 로직은 여기서 구현
 * @author 최병철
 *
 */
public class UserService {
	
	UserLevelUpgradePolicy policy;
	UserDao userDao;
	public void setPolicy(UserLevelUpgradePolicy policy){
		this.policy = policy;
	}
	
	/**
	 * xml로 DI
	 * @param userDao
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	/**
	 * 일괄적으로 모든 회원의 등급을 갱신하는 메소드
	 */
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users){
			if(canUpgradeLevel(user)){
				upgradeLevel(user);
				userDao.update(user);
			}
		}
	}
	



	/**
	 * 새로운 회원이 가입할때 등급 기본값을 BASIC으로 세팅
	 * @param user
	 */
	
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
}
