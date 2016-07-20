package user.dao;

import java.util.Map;

/**
 * 기존의 SqlRegistry의 조회 기능을 상속받고 추가적으로 Update 기능을 정의한 인터페이스
 * @author 최병철
 *
 */
public interface UpdatableSqlRegistry extends SqlRegistry {
	public void updateSql(String key, String sql) throws SqlUpdateFailureException;
	
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;

}
