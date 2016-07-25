package applicationcontext;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import user.dao.EmbeddedDbSqlRegistry;
import user.dao.OxmSqlService;
import user.dao.SqlRegistry;
import user.dao.SqlService;
import user.dao.UserDao;
import user.service.SqlMapConfig;

/**
 * SqlService는 다른 클라이언트도 사용이 가능하기 때문에 분리하는 것이 좋음
 * @author 최병철
 *
 */
@Configuration
public class SqlServiceContext {
	
	@Autowired
	SqlMapConfig sqlMapConfig;
	
	@Bean 
	public SqlService sqlService(){
		OxmSqlService oxmSqlService = new OxmSqlService();
		oxmSqlService.setUnmarshaller(unmarshaller());
		oxmSqlService.setSqlRegistry(sqlRegistry());
		oxmSqlService.setSqlmapFilePath(this.sqlMapConfig.getSqlMapConfig());
		return oxmSqlService;
	}
	
	
	
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(HSQL)
				.addScript("classpath:embeddeddb/schema.sql")
				.build();
	}
	
	@Bean 
	public SqlRegistry sqlRegistry(){
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(embeddedDatabase());
		return embeddedDbSqlRegistry;
	}
	
	@Bean
	public Unmarshaller unmarshaller(){
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath("user.sqlservice.jaxb");
		return jaxb2Marshaller;
	}
}
