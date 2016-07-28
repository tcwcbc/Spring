package vol2.learningtest.spring.ioc.setcontext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;


import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import vol2.learningtest.spring.ioc.setbean.AnnotatedHello;
import vol2.learningtest.spring.ioc.setbean.AnnotatedHelloConfig;
import vol2.learningtest.spring.ioc.setbean.HelloConfig;

public class IocTest {
	//현재 클래스의 패키지정보를 클래스패스 형식으로 만듦
	String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass()))+"/";
//	private static final String BASE_CONTEXT_LOCATION = "vol2/learningtest/spring/ioc/setcotext/";
	/**
	 * 빈 설정정보는 꼭 xml이나 class와 같은 형식이 아니더라도 BeanDefinition 형식의 정보를
	 * 읽을 수 있는 BeanDefinitionReader를 구현한 파일이라면 무엇이든 OK
	 */
	@Test
	public void beanTest(){
		//디폴트로 설정된 값을 사용
		StaticApplicationContext st = new StaticApplicationContext();
		st.registerSingleton("hello1", Hello.class);
		
		Hello hello1 = st.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));
		
		//Bean의 설정정보를 담은 틀을 생성
		BeanDefinition bd = new RootBeanDefinition(Hello.class);
		//빈의 프로퍼티 값 name을 설정
		bd.getPropertyValues().addPropertyValue("name", "Spring");
		//hello2라는 id값을 가진 빈을 추가
		st.registerBeanDefinition("hello2", bd);
		
		Hello hello2 = st.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));
		
		assertThat(hello1, is(not(hello2)));
		
		assertThat(st.getBeanFactory().getBeanDefinitionCount(), is(2));
	}
	/**
	 * 설정파일에서 런타임 시에 의존성 주입 테스트
	 * 다른 빈을 참조하기 위해 RuntimeBeanReference를 사용하여 주업
	 */
	@Test
	public void beanTestWithDependency(){
		StaticApplicationContext st = new StaticApplicationContext();
		st.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));
		BeanDefinition bd = new RootBeanDefinition(Hello.class);
		bd.getPropertyValues().addPropertyValue("name", "Spring");
		bd.getPropertyValues().add("printer", new RuntimeBeanReference("printer"));
		st.registerBeanDefinition("hello1", bd);
		
		Hello hello = st.getBean("hello1", Hello.class);
		hello.print();
		
		assertThat(st.getBean("printer").toString(), is("Hello Spring"));
	}
	
	/**
	 * XmlBeanDefinitionReader를 통하여 xml로 구성된 설정파일을 읽어오는 테스트
	 */
	@Test
	public void genericApplicationContext(){
//		org.springframework.context.support.GenericApplicationContext gac
//		= new org.springframework.context.support.GenericApplicationContext();
//		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(gac);
//		xmlReader.loadBeanDefinitions("vol2/learningtest/spring/ioc/GenericApplicationContext.xml");
//		gac.refresh();
		//위의 설정코드를 한줄로 끝.
		GenericXmlApplicationContext gac = 
				new GenericXmlApplicationContext(basePath+"GenericApplicationContext.xml");
		
		Hello hello = gac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(gac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	/**
	 * ApplicationContext(설정파일)을 상속을 맺어주고 자식컨텍스트에 없는 빈을 부모가 가지고있다면
	 * 가져와서 사용하는 것을 테스트
	 * 
	 * 자식은 부모의 것을 사용O, 부모는 자식의 것을 사용X
	 */
	@Test
	public void applicationContextInheritanceTest(){
		//부모 CTX를 설정해주고 자식의CTX를 reader로 읽을 때 추가해서 읽어준다.(상속설정)
		ApplicationContext parent = new GenericXmlApplicationContext(basePath+"ParentContext.xml");
		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath+"ChildContext.xml");
		//리더를 사용할 경우 반드시 refresh()
		child.refresh();
		
		Printer printer = child.getBean("printer", Printer.class);
		assertThat(printer, is(notNullValue()));
		
		Hello hello = child.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));
		
		//각 CTX에 name property를 다르게 설정해뒀기 때문에 Child가 출력되면 오바라이딩 된것이 맞음.
		hello.print();
		assertThat(printer.toString(), is("Hello Child"));
	}
	
	/**
	 * 빈 스캐닝 테스트, @Component 에서 빈ID를 등록 안하면 클래스 이름으로 자동 등록
	 */
	@Test
	public void simpleBeanScanning() {
		//설정한 하위 파일들에 대해서만 스캐닝
		ApplicationContext ctx = new AnnotationConfigApplicationContext("vol2.learningtest.spring.ioc.setbean");
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));
		
		AnnotatedHelloConfig annotatedHelloConfighelloConfig = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertThat(annotatedHelloConfighelloConfig, is(notNullValue()));
		//같은 오브젝트인지 확인
		assertThat(annotatedHelloConfighelloConfig.annotatedHello(), is(sameInstance(hello)));
	}

}
