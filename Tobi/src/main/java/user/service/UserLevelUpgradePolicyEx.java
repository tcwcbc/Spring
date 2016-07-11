package user.service;

import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

public class UserLevelUpgradePolicyEx implements UserLevelUpgradePolicy {
	public static final int MIN_LOGCOUNT_FOR_SILVER=50;
	public static final int MIN_RECOMMANDCOUNT_FOR_GOLD=30;
	
	
	/**
	 * 회원 등급 업그레이드 가능여부를 현재 등급에 따라 판단
	 * @param user
	 * @return	회원 업그레이드 가능여부
	 */
	@Override
	public boolean canUpgradeLevel(User user){
		Level currentLevel =user.getLevel(); 
		switch(currentLevel){
			case BASIC : return (user.getLogin()>=MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommand()>=MIN_RECOMMANDCOUNT_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Level : "+currentLevel);
		}
	}
	

	/**
	 * 현재 등급에 따라 업그레이드를 하고 회원 등급 갱신
	 * @param user
	 */
	@Override
	public void upgradeLevel(User user) {
		user.upgradeLevel();
	}

}
