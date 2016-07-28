package vol2.learningtest.spring.ioc.scope;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

/**
 * ApplicationContext의 빈의 Scope적용 테스트
 * 일반 IOC컨테이너(싱글톤)는 모든 빈을 싱글톤으로 컨테이너가 관리해서 DI DL되지만
 * @Scope("prototype")을 설정한 빈은 주입되고 나서 컨테이너가 관리안하고 DI받은 오브젝트 생명주기를 따라감
 * 
 * 프로토타입 빈은 Factory에서 new 연산자를 통하여 생성된 DTO인스턴스를 받는것과 비슷하지만
 * 객체 생성 시에 다른 빈으로부터 DI받아야 하는경우 사용하면 좋다.
 * 
 * 데이터중심설계 -> 객체중심 설계
 *  - DTO가 DAO를 DI 받아 setter 메소드 안에서 모든 정보들을 불러와
 *    하나의 데이터집합으로 만들어 놓으면 Service계층과 Presentation계층 을 분리하여
 *    유영하게 설계할 수 있음. 이때 DI받으려면 DTO를 빈으로 등록해야하는데 싱글톤으로 하면
 *    상태값을 제대로 보장 못하기 때문에 Scope를 Prototype으로 설정하여 사용.
 * 
 * @author 최병철
 *
 */
public class ScopeTest {
	@Test
	public void singletonScope(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(
				SingletonBean.class, SingletonClientBean.class);
		Set<SingletonBean> beans = new HashSet<ScopeTest.SingletonBean>();
		
		//DL에서 싱글톤확인
		beans.add(ac.getBean(SingletonBean.class));
		beans.add(ac.getBean(SingletonBean.class));
		assertThat(beans.size(), is(1));
		
		//DI에서 싱글톤 확인
		beans.add(ac.getBean(SingletonClientBean.class).sBean1);
		beans.add(ac.getBean(SingletonClientBean.class).sBean2);
		assertThat(beans.size(), is(1));
		
	}
	
	@Test
	public void prototypeScope(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(
				PrototypeBean.class, PrototypeBeanClient.class);
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		
		//DL에서 프로토타입확인
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(1));
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(2));
		
		//DI에서 프로토타입 확인
		beans.add(ac.getBean(PrototypeBeanClient.class).pBean1);
		assertThat(beans.size(), is(3));
		beans.add(ac.getBean(PrototypeBeanClient.class).pBean2);
		assertThat(beans.size(), is(4));
		
	}
	static class SingletonBean{}
	static class SingletonClientBean{
		@Autowired SingletonBean sBean1;
		@Autowired SingletonBean sBean2;
	}
	
	//스코프를 프로토타입으로 설정
	@Scope("prototype")
	static class PrototypeBean{}
	static class PrototypeBeanClient{
		@Autowired PrototypeBean pBean1;
		@Autowired PrototypeBean pBean2;
	}
	
}
