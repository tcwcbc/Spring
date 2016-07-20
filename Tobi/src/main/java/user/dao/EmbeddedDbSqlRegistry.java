package user.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 내장DB를 사용한 SqlRegistry
 * 트랜잭션은 싱글톤 빈으로 놓고 공유하여 사용할 필요가 없기 때문에
 * 설정파일에 빈으로 등록하지 않고 코드 내에서 생성하여 사용한다.
 * @author 최병철
 *
 */
public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry{
	SimpleJdbcTemplate jdbc;
	//Jdbc템플릿과 트랜잭션을 동기화햊는 트랜잭션템플릿
	TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource){
		jdbc = new SimpleJdbcTemplate(dataSource);
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
	}
	
	@Override
	public void registerSql(String key, String value) {
		jdbc.update("insert into sqlmap(key_, sql_) values(?,?)", key, value); 
		// TODO Auto-generated method stub
		
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		try{
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
		}catch(EmptyResultDataAccessException e){
			throw new SqlNotFoundException(key+"에 해당하는 SQL을 찾을 수 없습니다"+e);
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		// TODO Auto-generated method stub
		//반환되는 업데이트 갯수를 통하여 확인
		int affacted = jdbc.update("update sqlmap set sql_=? where key_=?", sql, key);
		if(affacted==0){
			throw new SqlUpdateFailureException(key+"에 해당하는 SQL을 찾을 수 없습니다.");
		}
	}

	//익명의 내부 클래스에서 사용하기 때문에 final 선언
	@Override
	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		// TODO Auto-generated method stub
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				// TODO Auto-generated method stub
				for(Map.Entry<String, String> entry : sqlmap.entrySet()){
					updateSql(entry.getKey(), entry.getValue());
				}
			}
		});
	}

}
