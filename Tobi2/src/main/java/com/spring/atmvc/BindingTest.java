package com.spring.atmvc;

import static org.hamcrest.CoreMatchers.instanceOf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.spring.mvc.AbstractDispatcherServletTest;
import com.spring.atmvc.Level;

/**
 * 프로퍼티 에디터를 활용한 바인딩 테스트
 * 
 * 커스텀 프로퍼티 에디터를 등록하는 방법
 * 
 * 1. 컨트롤러 내에서 initBinder()를 통하여 세팅
 * 	- 이렇게 등록하면 해당 컨트롤러로 들어오는 요청에만 적용
 * 
 * 2. WebBindingInitializer를 통한 등록
 * 	- 이렇게 등록하면 전체 컨트롤러에 적용됨. 프로퍼티명으로 구분하여 특정 프로퍼티에만 적용되게 할 수도 있음.
 * 
 * @author 최병철
 *
 */
public class BindingTest extends AbstractDispatcherServletTest {
	
	/**
	 * 기본적으로 등록된 프로퍼티에디터를 통한 바인딩 적용(CharSet)
	 * @author 최병철
	 *
	 */
	@Test
	public void defaultPropertyEditor() throws ServletException, IOException {
		setClasses(DefaultPEController.class);
		initRequest("/hello.do").addParameter("charset", "UTF-8");
		runService();
		assertModel("charset", Charset.forName("UTF-8"));
	}
	
	
	@Controller static class DefaultPEController {
		@RequestMapping("/hello") public void hello(@RequestParam Charset charset, Model model) {
			model.addAttribute("charset", charset);
		}
	}
	
	/**
	 * Charset 관련 PropertyEditor의 기능을 테스트하기 위한 단위 테스트
	 */
	@Test
	public void charsetEditor() {
		CharsetEditor charsetEditor = new CharsetEditor();
		charsetEditor.setAsText("UTF-8");
		assertThat(charsetEditor.getValue(), is(instanceOf(Charset.class)));
		assertThat((Charset)charsetEditor.getValue(), is(Charset.forName("UTF-8")));
	}

	/**
	 * Level 관련 PropertyEditor의 기능을 테스트하기 위한 단위 테스트
	 */
	@Test
	public void levelPropertyEditor() {
		LevelPropertyEditor levelEditor = new LevelPropertyEditor();
		
		levelEditor.setValue(Level.BASIC);
		assertThat(levelEditor.getAsText(), is("1"));
		
		levelEditor.setAsText("3");
		assertThat((Level)levelEditor.getValue(), is(Level.GOLD));
	}
	
	/**
	 * LevelPropertyEditor를 등록하고 정상적으로 동학하는지 확인하기 위한 테스트
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void levelTypeParameter() throws ServletException, IOException {
		setClasses(SearchController.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}
	@Controller static class SearchController {
		
		//@InitBider 애노테이션을 붙인 메소드에서 registerCustomEdior()를 통하여 적용할 프로퍼티 타입 클래스와 에디터를 등록
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		}
		
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	
	/**
	 * 프로퍼티에디터 인터페이스의 구현체인 PropertyEditorSupport를 상속받아 Customizing
	 * @author 최병철
	 *
	 */
	static class LevelPropertyEditor extends PropertyEditorSupport {
		//Level Enum의 값을 String 타입의 숫자로 변환
		public String getAsText() {
			return String.valueOf(((Level)this.getValue()).intValue());
		}
		
		//String 타입의 숫자를 Level Enum의 값으로 변경
		public void setAsText(String text) throws IllegalArgumentException {
			this.setValue(Level.valueOf(Integer.parseInt(text.trim())));
		}
	}
	
	/**
	 * webBindingInitilizer 테스트
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void webBindingInitializer() throws ServletException, IOException {
		//컨트롤러와 설정컨텍스트를 빈으로 등록
		setClasses(SearchController2.class, ConfigForWebBinidngInitializer.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}
	@Controller static class SearchController2 {
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	@Configuration static class ConfigForWebBinidngInitializer {
		@Bean public AnnotationMethodHandlerAdapter annotationMethodHandlerAdaptor() {
			return new AnnotationMethodHandlerAdapter() {{
				//WebBindingInitilaizer 등록
				setWebBindingInitializer(webBindingInitializer());
			}};
		}
		
		/**
		 * WebBindingInitializer에 추상메소드인 initBind메소드를 구현함(CustomPropertyEditor 등록)
		 * @return WebBindingInitializer
		 */
		@Bean public WebBindingInitializer webBindingInitializer() {
			return new WebBindingInitializer() {
				public void initBinder(WebDataBinder binder, WebRequest request) {
					binder.registerCustomEditor(Level.class,new LevelPropertyEditor());
				}
			};
		}
	}
	/*static class MyWebBindingInitializer implements WebBindingInitializer {
		public void initBinder(WebDataBinder binder, WebRequest request) {
			binder.registerCustomEditor(Level.class,new LevelPropertyEditor());
		}
	}*/
	
	/**
	 * 최종적으로 바인딩이 되는지 테스트
	 */
	@Test
	public void dataBinder() {
		WebDataBinder dataBinder = new WebDataBinder(null);
		//Level 프로퍼티 에디터와 각 프로퍼티 타입이 정의된 클래스 등록
		dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		assertThat(dataBinder.convertIfNecessary("1", Level.class), is(Level.BASIC));
	}
	
	/**
	 * PropertyEditor가 단순히 프로퍼티를 매핑시켜줄 뿐만 아니라 부가적인 필터 기능을 적용시킨 테스트
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void namedPropertyEditor() throws ServletException, IOException {
		setClasses(MemberController.class);
		initRequest("/add.do").addParameter("id", "10000").addParameter("age", "10000");
		runService();
		System.out.println(getModelAndView().getModel().get("member"));
	}
	@Controller static class MemberController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			//생성자로 최소값과 최대값을 받고 적용 될 프로퍼티이름과 타입을 넣어 설정
			dataBinder.registerCustomEditor(int.class, "age", new MinMaxPropertyEditor(0, 200));
		}
		@RequestMapping("/add") public void add(@ModelAttribute Member member) {
		}
	}
	static class Member {
		int id;
		int age;
		public int getId() { return id; }
		public void setId(int id) { this.id = id; }
		public int getAge() { return age; }
		public void setAge(int age) { this.age = age; }
		public String toString() { return "Member [age=" + age + ", id=" + id + "]"; }
		
	}
	
	/**
	 * 최대값과 최소값을 받아 파라미터로 받은 값이 범위를 넘으면 최소,최대 값으로 설정해주는 프로퍼티 에디터
	 * @author 최병철
	 *
	 */
	static class MinMaxPropertyEditor extends PropertyEditorSupport {
		int min = Integer.MIN_VALUE;
		int max = Integer.MAX_VALUE;
		
		public MinMaxPropertyEditor(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		public MinMaxPropertyEditor() { }

		public String getAsText() {
			return String.valueOf((Integer)this.getValue());
		}

		//최대값과 최소값을 설정하는 로직
		public void setAsText(String text) throws IllegalArgumentException {
			Integer val = Integer.parseInt(text);
			if (val < min) val = min;
			else if (val > max) val = max;
			
			setValue(val);
		}
	}
	/////////////////////////////////////////////////////////////////////////
	/**
	 * 프로퍼티 에디터에서 컨텍스트에 등록된 빈을 사용해야 할 경우 자기 자신도 빈으로 등록되어야 함.
	 * 그런데, 빈으로 등록하면 싱글톤 -> 프로퍼티에디터는 상태값을 갖기 때문에 싱글톤X, 프로토터입 빈O
	 * 
	 * Ex) 다른 도메인을 참조하는 도메인 오브젝트를 통한 바인딩	
	 * 				User의 프로퍼티 중 Code code; 프로퍼티...
	 * 	해결방안 3가지
	 * 		1. 별도의 int codeId 프로퍼티를 만들어 놓고 codeId를 통해 서비스 단에서 Code 도메인 오브젝트를 가져와 세팅.
	 * 			-> 단점 : 불필요한 임시 프로퍼티 추가로 인한 가독성 저하, DB Access 횟수 +1
	 * 		2. 모조 오브젝트 프로퍼티 에디터를 활용.
	 * 			-> 단점 : Code 오브젝트의 id값만 있고 나머지는 다 null 이기 때문에 Code 오브젝트의 다른 프로퍼티들을
	 * 					참조하는 코드에서 NullPointException 발생 가능성이 높음. 
	 * 					매번 Code 오브젝트를 가져오는 값을 수정하기 때문에 결합도가 강해짐 
	 * 		3. 프로퍼티에디터를 프로토타입 빈으로 등록시켜놓고 DI받는 방법(2번과 유사)
	 * 			-> Provider사용
	 * @author 최병철
	 *
	 */
	////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void webBindingInit() throws ServletException, IOException {
		setClasses(Config.class, UserController.class);
		initRequest("/add.do", "POST");
		addParameter("id", "1").addParameter("name", "Spring").addParameter("date", "02/03/01");
		addParameter("level", "3");
		runService();
	}
	
	
	@Controller static class UserController {
		@RequestMapping("/add") public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}
	
	
	@Configuration static class Config {
		@Autowired FormattingConversionService conversionService;
		
		@Bean public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
			return new AnnotationMethodHandlerAdapter() {{
				setWebBindingInitializer(webBindingInit());
			}};
		}
		
		@Bean public WebBindingInitializer webBindingInit() {
			return new ConfigurableWebBindingInitializer() {{
				setConversionService(Config.this.conversionService);
			}};
		}
		
		@Bean public FormattingConversionServiceFactoryBean formattingConversionServiceFactoryBean() {
			return new FormattingConversionServiceFactoryBean() {{
//				setConverters(new LinkedHashSet(Arrays.asList(new Converter[] {new LabelToStringConverter(), new StringToLabelConverter()}))); // convert 적용
				}
				protected void installFormatters(FormatterRegistry registry) {
					super.installFormatters(registry);
					registry.addFormatterForFieldType(Level.class, new LabelStringFormatter());
				}
			};
		}
		
		// formatter
		static class LabelStringFormatter implements Formatter<Level> {
			public String print(Level level, Locale locale) {
				return String.valueOf(level.intValue());
			}

			public Level parse(String text, Locale locale) throws ParseException {
				return Level.valueOf(Integer.parseInt(text));
			}
		}
		
		// converter
		static class LevelToStringConverter implements Converter<Level, String> {
			public String convert(Level level) {
				return String.valueOf(level.intValue());
			}
		}
		
		static class StringToLevelConverter implements Converter<String, Level> {
			public Level convert(String text) {
				return Level.valueOf(Integer.parseInt(text));
			}
		}
	}
	static class User {
		int id;
		String name;
		Level level;
		@DateTimeFormat(pattern="dd/yy/MM")
		Date date;
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Level getLevel() {
			return level;
		}
		public void setLevel(Level level) {
			this.level = level;
		}
		@Override
		public String toString() {
			return "User [date=" + date + ", id=" + id + ", level=" + level + ", name=" + name + "]";
		}
	}
}
