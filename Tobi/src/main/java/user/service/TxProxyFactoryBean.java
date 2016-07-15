package user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {

	Object target;

	PlatformTransactionManager transactionManager;

	String pattern;

	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		// 리플렉션을 활용한 TransactionHandler 다이나믹 프록시
		TransactionHandler transactionHandler = new TransactionHandler();
		transactionHandler.setTransactionManager(this.transactionManager);
		transactionHandler.setPattern(pattern);
		transactionHandler.setTarget(target);

		return Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { serviceInterface }, transactionHandler);
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
