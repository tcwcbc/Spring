package user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import user.domain.User;

/**
 * UserServic의 트랜잭션을 위한 UserService 구현 클래스
 * add나 upgradeLevels 와 같은 비즈니스 로직은
 * DI를 통해 받은 UserService의 구현체로 위임
 * @author 최병철
 *
 */
public class UserServiceTx implements UserService{

	UserService userService;

	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager){
		this.transactionManager = transactionManager;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void add(User user) {
		// TODO Auto-generated method stub
		userService.add(user);
		
	}

	@Override
	public void upgradeLevels() {
		// TODO Auto-generated method stub

		//트랜잭션 경계설정
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		
		try{
			userService.upgradeLevels();		
			this.transactionManager.commit(status);
//		c.commit();
		}catch(Exception e){
			//에러시 롤백
//			c.rollback();
			this.transactionManager.rollback(status);
			
//			throw e;
		}finally {
		}
		
		
	}

}
