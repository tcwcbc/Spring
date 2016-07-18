package user.dao;

import javax.annotation.PostConstruct;

/**
 * 확장 가능한 기반 클래스
 * 기존의 XmlSqlService에서 세가지 구현체를 한번에 구현하고 Bean설정을 자기참조로 했다면
 * BasicSqlService, HashMapSqlRegistry, JaxbXmlSqlReader로 분할하여 DI 하는 방법
 * @author 최병철
 *
 */
public class BasicSqlService implements SqlService{

	//상속을 통한 확장을 위해 protected
	protected SqlReader sqlReader;
	protected SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	@PostConstruct
	public void loadSql(){
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// TODO Auto-generated method stub
		try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}
	}

}
