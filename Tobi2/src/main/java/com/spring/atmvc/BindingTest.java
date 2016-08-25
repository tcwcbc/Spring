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
 * ������Ƽ �����͸� Ȱ���� ���ε� �׽�Ʈ
 * 
 * Ŀ���� ������Ƽ �����͸� ����ϴ� ���
 * 
 * 1. ��Ʈ�ѷ� ������ initBinder()�� ���Ͽ� ����
 * 	- �̷��� ����ϸ� �ش� ��Ʈ�ѷ��� ������ ��û���� ����
 * 
 * 2. WebBindingInitializer�� ���� ���
 * 	- �̷��� ����ϸ� ��ü ��Ʈ�ѷ��� �����. ������Ƽ������ �����Ͽ� Ư�� ������Ƽ���� ����ǰ� �� ���� ����.
 * 
 * @author �ֺ�ö
 *
 */
public class BindingTest extends AbstractDispatcherServletTest {
	
	/**
	 * �⺻������ ��ϵ� ������Ƽ�����͸� ���� ���ε� ����(CharSet)
	 * @author �ֺ�ö
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
	 * Charset ���� PropertyEditor�� ����� �׽�Ʈ�ϱ� ���� ���� �׽�Ʈ
	 */
	@Test
	public void charsetEditor() {
		CharsetEditor charsetEditor = new CharsetEditor();
		charsetEditor.setAsText("UTF-8");
		assertThat(charsetEditor.getValue(), is(instanceOf(Charset.class)));
		assertThat((Charset)charsetEditor.getValue(), is(Charset.forName("UTF-8")));
	}

	/**
	 * Level ���� PropertyEditor�� ����� �׽�Ʈ�ϱ� ���� ���� �׽�Ʈ
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
	 * LevelPropertyEditor�� ����ϰ� ���������� �����ϴ��� Ȯ���ϱ� ���� �׽�Ʈ
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
		
		//@InitBider �ֳ����̼��� ���� �޼ҵ忡�� registerCustomEdior()�� ���Ͽ� ������ ������Ƽ Ÿ�� Ŭ������ �����͸� ���
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		}
		
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	
	/**
	 * ������Ƽ������ �������̽��� ����ü�� PropertyEditorSupport�� ��ӹ޾� Customizing
	 * @author �ֺ�ö
	 *
	 */
	static class LevelPropertyEditor extends PropertyEditorSupport {
		//Level Enum�� ���� String Ÿ���� ���ڷ� ��ȯ
		public String getAsText() {
			return String.valueOf(((Level)this.getValue()).intValue());
		}
		
		//String Ÿ���� ���ڸ� Level Enum�� ������ ����
		public void setAsText(String text) throws IllegalArgumentException {
			this.setValue(Level.valueOf(Integer.parseInt(text.trim())));
		}
	}
	
	/**
	 * webBindingInitilizer �׽�Ʈ
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void webBindingInitializer() throws ServletException, IOException {
		//��Ʈ�ѷ��� �������ؽ�Ʈ�� ������ ���
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
				//WebBindingInitilaizer ���
				setWebBindingInitializer(webBindingInitializer());
			}};
		}
		
		/**
		 * WebBindingInitializer�� �߻�޼ҵ��� initBind�޼ҵ带 ������(CustomPropertyEditor ���)
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
	 * ���������� ���ε��� �Ǵ��� �׽�Ʈ
	 */
	@Test
	public void dataBinder() {
		WebDataBinder dataBinder = new WebDataBinder(null);
		//Level ������Ƽ �����Ϳ� �� ������Ƽ Ÿ���� ���ǵ� Ŭ���� ���
		dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		assertThat(dataBinder.convertIfNecessary("1", Level.class), is(Level.BASIC));
	}
	
	/**
	 * PropertyEditor�� �ܼ��� ������Ƽ�� ���ν����� �Ӹ� �ƴ϶� �ΰ����� ���� ����� �����Ų �׽�Ʈ
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
			//�����ڷ� �ּҰ��� �ִ밪�� �ް� ���� �� ������Ƽ�̸��� Ÿ���� �־� ����
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
	 * �ִ밪�� �ּҰ��� �޾� �Ķ���ͷ� ���� ���� ������ ������ �ּ�,�ִ� ������ �������ִ� ������Ƽ ������
	 * @author �ֺ�ö
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

		//�ִ밪�� �ּҰ��� �����ϴ� ����
		public void setAsText(String text) throws IllegalArgumentException {
			Integer val = Integer.parseInt(text);
			if (val < min) val = min;
			else if (val > max) val = max;
			
			setValue(val);
		}
	}
	/////////////////////////////////////////////////////////////////////////
	/**
	 * ������Ƽ �����Ϳ��� ���ؽ�Ʈ�� ��ϵ� ���� ����ؾ� �� ��� �ڱ� �ڽŵ� ������ ��ϵǾ�� ��.
	 * �׷���, ������ ����ϸ� �̱��� -> ������Ƽ�����ʹ� ���°��� ���� ������ �̱���X, ���������� ��O
	 * 
	 * Ex) �ٸ� �������� �����ϴ� ������ ������Ʈ�� ���� ���ε�	
	 * 				User�� ������Ƽ �� Code code; ������Ƽ...
	 * 	�ذ��� 3����
	 * 		1. ������ int codeId ������Ƽ�� ����� ���� codeId�� ���� ���� �ܿ��� Code ������ ������Ʈ�� ������ ����.
	 * 			-> ���� : ���ʿ��� �ӽ� ������Ƽ �߰��� ���� ������ ����, DB Access Ƚ�� +1
	 * 		2. ���� ������Ʈ ������Ƽ �����͸� Ȱ��.
	 * 			-> ���� : Code ������Ʈ�� id���� �ְ� �������� �� null �̱� ������ Code ������Ʈ�� �ٸ� ������Ƽ����
	 * 					�����ϴ� �ڵ忡�� NullPointException �߻� ���ɼ��� ����. 
	 * 					�Ź� Code ������Ʈ�� �������� ���� �����ϱ� ������ ���յ��� ������ 
	 * 		3. ������Ƽ�����͸� ������Ÿ�� ������ ��Ͻ��ѳ��� DI�޴� ���(2���� ����)
	 * 			-> Provider���
	 * @author �ֺ�ö
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
//				setConverters(new LinkedHashSet(Arrays.asList(new Converter[] {new LabelToStringConverter(), new StringToLabelConverter()}))); // convert ����
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
