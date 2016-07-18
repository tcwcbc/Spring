package user.dao;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import user.dao.UserDao;
import user.sqlservice.jaxb.SqlType;
import user.sqlservice.jaxb.Sqlmap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.annotation.PostConstruct;

/**
 * xml 파일의 sql을 언마샬링 하여 객체화 시키는 클래스
 * 
 * SqlRegistry와 SqlReader 인터페이스 각각의 추상 메소드를 구현하고 각자의 메소드 및
 * 해당 메소드에서 사용되는 변수는 클래스 내에서 공유하지 않고 인터페이를 통해 접근한다.(한 클래스 안에 별도의 객체 처럼)
 * sqlRegistry의 메소드가 콜백처럼 작동
 * @author 최병철
 *
 */
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
	
	private SqlReader sqlReader;
	
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	private String sqlmapFilePath;
	
	public void setSqlmapFilePath(String sqlmapFilePath){
		this.sqlmapFilePath = sqlmapFilePath;
	}
	
	//sql을 저장할 맵
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
//	//매번 sqlmap.xml을 읽어오지 않고 이 객체가 생성될 때 한번만 읽어옴
//	public XmlSqlService() {
//		// TODO Auto-generated constructor stub
//		loadSql();
//	}

	//생성자에서 예외가 발생하면 다루기 힘들고 상속이 어렵기 때문에 별도의 메소드로 작성
	//초기화 메소드이기 때문에 항상 먼저 실행 되어야 하는데 이는 설정파일에서 빈 후처리기 사용
//	@PostConstruct
//	private void loadSql() {
//		String contextPath = Sqlmap.class.getPackage().getName();
//		try{
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFilePath);
//			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
//			
//			for(SqlType sql : sqlmap.getSql()){
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//			
//		}catch(JAXBException e){
//			//복구 불가능한 예외이기에 포장해서 던짐
//			throw new RuntimeException(e);
//		}
//	}
	
	//인터페이스를 통해 접근, 구현방법
	@PostConstruct
	public void loadSql(){
		this.sqlReader.read(this.sqlRegistry);
	}
	
	
	
	//SqlReader와 SqlRegistry 인터페이스를 구현하지 않고 자체적으로 만든 메소드
//	@Override
//	public String getSql(String key) throws SqlRetrievalFailureException {
//		// TODO Auto-generated method stub
//		String sql = sqlMap.get(key);
//		if(sql==null){
//			throw new SqlRetrievalFailureException(key+"를 이용해서 SQL을 찾을 수 없습니다.");
//		}else{
//			
//			return sql;
//		}
//	}

	//SqlRegistry의 구현체
	@Override
	public void registerSql(String key, String value) {
		// TODO Auto-generated method stub
		sqlMap.put(key, value);
	}

	//SqlRegistry의 구현체
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		if(sql==null){
			throw new SqlRetrievalFailureException(key+"를 이용해서 SQL을 찾을 수 없습니다.");
		}else{
			
			return sql;
		}
	}

	//SqlService의 구현체
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// TODO Auto-generated method stub
		try{
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e){
			throw new SqlRetrievalFailureException(e);
		}
	}

	//SqlReader의 구현체
	//기존의 초기화 메소드를 가져온다.
	@Override
	public void read(SqlRegistry registry) {
		// TODO Auto-generated method stub
		String contextPath = Sqlmap.class.getPackage().getName();
		try{
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFilePath);
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()){
				registry.registerSql(sql.getKey(), sql.getValue());
			}
			
		}catch(JAXBException e){
			//복구 불가능한 예외이기에 포장해서 던짐
			throw new RuntimeException(e);
		}
	}

}
