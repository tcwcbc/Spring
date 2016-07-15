package user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler {
	//부가기능을 제공할 타겟 오브젝트
	private Object target;
	
	private PlatformTransactionManager transactionManager;
	
	//트랜잭션을 적용할 메소드 이름
	private String pattern;
	
	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 트랜잭션 적용 될 메소드를 선별
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		if(method.getName().startsWith(pattern)){
			return invokeInTransaction(method, args);
		}else{
			return method.invoke(target, args);
		}
	}

	/**
	 *  모든 메소드를 리플렉션을 통하여 트랜잭션을 적용시킬 수 있는 메소드
	 * @param method 타겟 메소드
	 * @param args 파라미터
	 * @return 예외가 발생 안하면 트랜잭션 commit, 에외가 발생하면 rollback
	 * @throws Throwable
	 */
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try{
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
			return ret;
		}catch ( InvocationTargetException e){
			this.transactionManager.rollback(status);
			throw e.getTargetException();
		}
	}

}
