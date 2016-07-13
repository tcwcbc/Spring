package proxcy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 리플렉션을 활용하여 객체에 대한 정보를 얻어오고 해당 객체에 선언된 메소드들을
 * 호출해주는 핸들러
 * 
 * InvocationHandler를 상속받으면 많은 메소드들을 invoke메소드 하나만으로
 * 모두 호출할 수 있다.
 * @author 최병철
 *
 */
public class UppercaseHandler implements InvocationHandler{

	Object target;
	
	public UppercaseHandler(Object hello) {
		// TODO Auto-generated constructor stub
		this.target = hello;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		// TODO Auto-generated method stub
		String ret = (String) method.invoke(target, args);
		if(ret instanceof String && method.getName().startsWith("say")){
			return ret.toUpperCase();
		}else{
			return ret;
		}
	}

}
