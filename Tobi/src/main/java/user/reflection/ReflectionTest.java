package user.reflection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class ReflectionTest {
	
	@Test
	public void invokeMethod() throws Exception{
		String name = "Spring";
		
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		
		assertThat(name.charAt(0), is('S'));
		
		Method charAt = String.class.getMethod("charAt", int.class);
		assertThat((Character)charAt.invoke(name, 0), is('S'));
	}
}
