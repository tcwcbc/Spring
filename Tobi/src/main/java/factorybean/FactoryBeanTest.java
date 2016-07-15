package factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//설정하지 않으면 디폴트로 클래스명-context.xml로 바인딩 됨
@ContextConfiguration
public class FactoryBeanTest {

	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactoryBean() {
		//message는 팩토리빈이 getObject로 반환해주는 객체
		Object message = context.getBean("message");
//		assertThat(message, is(Message.class));
		assertThat(((Message)message).getText(), is("Factory Bean"));
	}
	
	@Test
	public void getFactoryBean() throws Exception {
		//여기서 factory는 팩토리 빈 자체
		Object factory = context.getBean("&message");
//		assertThat(factory, is(MessageFactoryBean.class));
	}


}
