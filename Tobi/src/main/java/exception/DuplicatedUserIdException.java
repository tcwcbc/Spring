package exception;


/**
 *	아이디 중복 예외 
 * @author 최병철
 *
 */
public class DuplicatedUserIdException extends RuntimeException{
	public DuplicatedUserIdException(Throwable cause){
		super(cause);
	}
}
