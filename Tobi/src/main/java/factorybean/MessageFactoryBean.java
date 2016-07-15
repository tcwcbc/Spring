package factorybean;

import org.springframework.beans.factory.FactoryBean;

/**
 * 빈으로 등록하기 위한 FactoryBean 클래스
 * 단순히 등록된 빈을 생성할 때만 사용 됨
 * FactoryBean을 구현한 클래스를 ApplicationContext에 등록하면
 * getobject 메소드를 사용하여 빈을 생성
 * @author 최병철
 *
 */
public class MessageFactoryBean implements FactoryBean<Message> {
	String text;
	
	//빈 프로퍼티를 사용한 DI를 위한 세터
	public void setText(String text){
		this.text = text;
	}
	
	//오브젝트 생성
	@Override
	public Message getObject() throws Exception {
		// TODO Auto-generated method stub
		return Message.newMessage(text);
	}

	@Override
	public Class<? extends Message> getObjectType() {
		// TODO Auto-generated method stub
		return Message.class;
	}

	//getobject메소드가 매번 새로운 객체를 생성하기 때문에 싱글톤X
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
