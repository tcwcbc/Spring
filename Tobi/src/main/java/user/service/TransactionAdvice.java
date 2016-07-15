package user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring의 Advice Interface를 상속받은 methodInterceptor를 구현
 * @author 최병철
 *
 */
public class TransactionAdvice implements MethodInterceptor{
	PlatformTransactionManager transactionManager;
	
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}


	/**
	 * 타겟을 호출하는 기능을 가진 invocation 오브젝트를 프록시로부터 받는다.
	 * 특정 타깃에 의존되지 않고 재사용 가능
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// TODO Auto-generated method stub
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try{
			//콜백 오브젝트를 통해 위임 , 이 메소드 전 후로 부가기능을 추가하면 됨
			Object ret = invocation.proceed();
			this.transactionManager.commit(status);
			return ret;
			//다이나믹 프록시와는 다르게 예외를 포장하여 보내지 않음
		}catch (RuntimeException e){
			this.transactionManager.rollback(status);
			throw e;
		}
		
	}

}
