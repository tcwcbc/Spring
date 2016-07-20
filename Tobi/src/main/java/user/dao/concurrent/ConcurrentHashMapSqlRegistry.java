package user.dao.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import user.dao.SqlNotFoundException;
import user.dao.SqlUpdateFailureException;
import user.dao.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();
	
	@Override
	public void registerSql(String key, String value) {
		// TODO Auto-generated method stub
		sqlMap.put(key, value);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		if(sql==null){
			throw new SqlNotFoundException(key+"를 이용해서 SQL을 찾을 수 없습니다.");
		}
			return sql;
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		// TODO Auto-generated method stub
		if(sqlMap.get(key)==null){
			throw new SqlUpdateFailureException(key+"에 해당하는 SQL을 찾을 수 없습니다.");
		}
			sqlMap.put(key, sql);
	}

	@Override
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
		// TODO Auto-generated method stub
		for(Map.Entry<String, String> entry : sqlmap.entrySet()){
			updateSql(entry.getKey(), entry.getValue());
		}
	}
}
