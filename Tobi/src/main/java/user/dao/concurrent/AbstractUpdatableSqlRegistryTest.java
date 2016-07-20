package user.dao.concurrent;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import user.dao.SqlNotFoundException;
import user.dao.SqlUpdateFailureException;
import user.dao.UpdatableSqlRegistry;

/**
 * 읽기전용이라면 상관없지만 수정과 같은 작업들이 이루어질 때 동시성을 보장하는지
 * 테스트하는 클래스
 * 추상화를 통하여 UpdatableSqlRegistry 인터페이스를 구현한 클래스들을 테스트
 * @author 최병철
 *
 */
public abstract class AbstractUpdatableSqlRegistryTest {
	protected UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp(){
//		sqlRegistry = new ConcurrentHashMapSqlRegistry();
		sqlRegistry = createUpdatableSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();
	
	@Test
	public void find(){
		checkFindResult("SQL1", "SQL2", "SQL3");
	}
	
	protected void checkFindResult(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}
	
	//없는 키로 조회를 했을 때
	@Test(expected= SqlNotFoundException.class)
	public void unknownKey(){
		sqlRegistry.findSql("SQL9999!@#$");
	}
	
	//하나만 업데이트 하고 난 후 테스트
	@Test
	public void updateSingle(){
		sqlRegistry.updateSql("KEY2", "Modified2");
		checkFindResult("SQL1", "Modified2", "SQL3");
	}
	
	//여러개를 map에 담아 한번에 update한 후 테스트
	@Test
	public void updateMultiple(){
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFindResult("Modified1", "SQL2", "Modified3");
	}
	
	//존재하지 않는 키 갑으로 update 시도시 예외발생여부 테스트
	@Test(expected=SqlUpdateFailureException.class)
	public void updateWithNotExistingKey(){
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
}
