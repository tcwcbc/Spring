package com.spring.atmvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.mvc.AbstractDispatcherServletTest;


/**
 *  * [Fomatter]
 * Locale 정보를 통해 다국어 지원을 위할 때 유용.
 * 3.1에서는 Formatter의 사용이 제법 번거롭기 때문에  GenericConversionService를 구현한
 * FomattingConversionService(FactoryBean)을 활용하여 사용.(디폴트 메소드들)
 * 
 * @NumberFormat - 단순 타입의 숫자를 변환 (다국어 통화)
 * @DateTimeFormatter 날짜 및 시간 변환.
 * 
 * 
 * @author 최병철
 *
 */
public class FormatterTest extends AbstractDispatcherServletTest {
	@Test
	public void numberFormat() throws ServletException, IOException {
		setRelativeLocations("mvc-annotation.xml");
		setClasses(UserController.class);
		initRequest("/hello.do").addParameter("money", "$1,234.56");
		runService();
	}
	
	@Test
	public void dateFormat() throws ServletException, IOException {
		setRelativeLocations("mvc-annotation.xml");
		setClasses(UserController.class);
		initRequest("/hello.do").addParameter("date", "01/02/1999");
		runService();
	}
	
	static class User {
		@DateTimeFormat(pattern="dd/MM/yyyy")
		Date date;
		public Date getDate() { return date; }
		public void setDate(Date date) { this.date = date; }
		
		@NumberFormat(pattern="$###,##0.00")
		BigDecimal money;
		public BigDecimal getMoney() { return money; }
		public void setMoney(BigDecimal money) { this.money = money; }
		
	}
	@Controller static class UserController {
		@RequestMapping("/hello") void hello(User user) {
			System.out.println(user.date);
			System.out.println(user.money);
		}
	}
	
	@Test
	public void dateTimeFormat() {
		System.out.println(org.joda.time.format.DateTimeFormat.patternForStyle("SS", Locale.US));
	}
}
