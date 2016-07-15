package pointcut.excution;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {
	@Test
	public void methodSignaturePointcut() throws Exception{
		targetClassPointcutMatches("execution(* *(..))", true,true,true,true,true,true);
	}
	
	public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception{
		pointcutMatches(expression, expected[0], Target.class, "hello");
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		pointcutMatches(expression, expected[4], Target.class, "method");
		pointcutMatches(expression, expected[4], Bean.class, "method");
	}
	
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, 
			String methodName, Class<?>...args) throws Exception{
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) &&
				pointcut.getMethodMatcher().matches(clazz.getMethod(methodName,args),null), 
				is(expected));
	}
}
