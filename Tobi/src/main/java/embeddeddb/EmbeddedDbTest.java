package embeddeddb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * 내장형 DB 테스트
 * JDBC API와 DataSource 오브젝트를 활용하여 내장형 DB를 사용할 수 있음
 * 애플리케이션 시작 시 메모리에 DB가 생성되고 종료시 ShutDown. 
 * @author 최병철
 *
 */
public class EmbeddedDbTest {
	//DataSourc 인터페이스를 상속하고 ShutDown()를 구현한 클래스
	EmbeddedDatabase db;
	SimpleJdbcTemplate template;
	
	@Before
	public void setUp(){
		//빌더를 통해 내장DB type을 정하고 sql문을 읽어와 초기화
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("classpath:embeddeddb/schema.sql")
				.addScript("classpath:embeddeddb/data.sql")
				.build();
		
		template = new SimpleJdbcTemplate(db);
	}
	
	//종료시에 shutDown()으로 자원 반납
	@After
	public void tearDown(){
		db.shutdown();
	}
	
	@Test
	public void initData(){
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
		assertThat((String)list.get(0).get("key_"), is("KEY1"));
		assertThat((String)list.get(0).get("sql_"), is("SQL1"));
		assertThat((String)list.get(1).get("key_"), is("KEY2"));
		assertThat((String)list.get(1).get("sql_"), is("SQL2"));
	}
	
	@Test
	public void insert(){
		template.update("insert into sqlmap(key_, sql_) values(?,?)", "KEY3", "SQL3");
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(3));
	}
}
