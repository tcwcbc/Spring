package user.dao;

/**
 * Sql 문을 가져올 때 실패할 경우 예외
 * @author 최병철
 *
 */
public class SqlRetrievalFailureException extends RuntimeException {
	
	public SqlRetrievalFailureException(String message){
		super(message);
	}
	public SqlRetrievalFailureException(String message, Throwable cause){
		super(message, cause);
	}
	public SqlRetrievalFailureException(SqlNotFoundException e) {
		// TODO Auto-generated constructor stub
		super(e);
	}

}
