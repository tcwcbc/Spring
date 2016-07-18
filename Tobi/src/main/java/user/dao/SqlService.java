package user.dao;

/**
 * sql문을 가져오는 인터페이스
 * @author 최병철
 *
 */
public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
}
