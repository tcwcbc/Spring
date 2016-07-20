package user.dao;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import user.dao.concurrent.AbstractUpdatableSqlRegistryTest;

/**
 * 내장 DB를 사용한 SqlRegistry 클래스를 테스트
 * 내장 DB를 초기화해주는 빌더가 세팅되어야 하고 끝난 후에는 shutDown()으로 자원 해제
 * @author 최병철
 *
 */
public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
	EmbeddedDatabase db;
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		// TODO Auto-generated method stub
		db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("classpath:embeddeddb/schema.sql")
				.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		return embeddedDbSqlRegistry;
	}
	
	@After
	public void tearDown(){
		db.shutdown();
	}

	//내장DB의 트랜잭션 적용을 확인하기 위한 테스트
	@Test
	public void transactionalUpdate(){
		checkFindResult("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY!@#$%%", "Modified@#!#");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			fail();
		} catch (SqlUpdateFailureException e) {
			// TODO: handle exception
			checkFindResult("SQL1", "SQL2", "SQL3");
		}
	}
}
