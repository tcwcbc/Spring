package com.spring.atmvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.atmvc.Level;
import com.spring.mvc.AbstractDispatcherServletTest;;

/**
 * 프로퍼티 에디터는 이렇게 상태를 갖는 빈이기 때문에 멀티스레드 환경에서 싱글톤 빈으로 작동하면 문제가 발생하기 때문에
 * 프로토타입 빈으로 설정해줘야 하며 항상 오브젝트를 생성해야함. 
 * 이를 해결하기 위해 3.0부터 [Converter]와 [Fomatter]를 사용.
 * 
 * [Converter]
 * '->' 컨버터와 '<-'컨버터를 ConversionService를 통해 등록하면 프로퍼티 에디터와 유사하게 동작함.
 * {@link Converter}
 * 
 * ConversionService를 사용하는 2가지 방법
 * 1. @initBinder 를 통해 수동으로 주입.
 * 	- GenericConversionService를 상속받아 addConversionService()로 Converter들을 등록하거나
 * 		ConversionServiceFactoryBean을 빈으로 등록시키고 DI받아 사용한 뒤에 컨트롤러마다 @initBinder로 수동등록.
 * 2. WebBinddingInitializer와 같은 일괄등록
 *  - ConfigurableWebBindingInitializer의 webBindingInitializer의 프로퍼티에 참조시켜 등록.
 * 

 */
public class ConverterTest extends AbstractDispatcherServletTest {
	@Test
	public void inheritedGenericConversionService() throws ServletException, IOException {
		setClasses(SearchController.class, MyConvertsionService.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}
	@Controller public static class SearchController {
		@Autowired ConversionService conversionService;
		
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setConversionService(this.conversionService);
		}
		
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	static class MyConvertsionService extends GenericConversionService {{
		this.addConverter(new LevelToStringConverter());
		this.addConverter(new StringToLevelConverter());
	}}
	@SuppressWarnings("unchecked")
	static class MyConversionServiceFactoryBean extends ConversionServiceFactoryBean {{
		this.setConverters(new LinkedHashSet(Arrays.asList(
				new Converter[] { new LevelToStringConverter(), new StringToLevelConverter()} )));
	}}
	public static class LevelToStringConverter implements Converter<Level, String> {
		public String convert(Level level) {
			return String.valueOf(level.intValue());
		}
	}
	public static class StringToLevelConverter implements Converter<String, Level> {
		public Level convert(String text) {
			return Level.valueOf(Integer.parseInt(text));
		}
	}
	
	@Test 
	public void compositeGenericConversionService() throws ServletException, IOException {
		setRelativeLocations("conversionservice.xml");
		setClasses(SearchController.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}
}
