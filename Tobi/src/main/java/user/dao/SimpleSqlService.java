package user.dao;

import java.util.Map;

/**
 * UserDao에 주입하여 sql을 받아오는 로직을 담은 sqlsService 구현체
 * 직접 설정파일로부터 DI 받음
 * @author 최병철
 *
 */
public class SimpleSqlService implements SqlService{
	private Map<String, String> sqlMap;
	//sqlMap을 DI 받음
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		if(sql==null){
			throw new SqlRetrievalFailureException(key+"에 대한 SQL을 찾을 수 없습니다.");
		}else{
			return sql;
		}
	}
}
