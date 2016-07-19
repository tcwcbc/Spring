package user.dao;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import user.sqlservice.jaxb.SqlType;
import user.sqlservice.jaxb.Sqlmap;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * OxmSqlService 와 OxmSqlReader는 강하게 결합되나 하나의 빈으로 설정
 * 
 * 등록해야할 빈의 갯수를 줄여 최적화
 * @author 최병철
 */
public class OxmSqlService implements SqlService{
	
	//loadSql과 getSql의 중복코드를 위임하여 처리하기 위해 BasicSqlService 생성;
	private final BasicSqlService basicSqlService = new BasicSqlService();
	
	//final임으로 변경 불가능
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	//디폴트 오브젝트로 만들어진 프로퍼티, 추후에 DI로 변경 가능
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller){
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	//내부 오브젝트의 인스턴스 변수에 DI해주는 setter
	//웹, 파일시스템, 클래스패스등의 경로에서 sqlMap을 가져오기 위해 Resource타입으로 선언
	public void setSqlmapFilePath(Resource sqlmap){
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	@PostConstruct
	public void loadSql(){
		//basicSqlService에 SqlReader와 sqlRegistry를 주입하여 위임
		this.basicSqlService.setSqlReader(this.oxmSqlReader);
		this.basicSqlService.setSqlRegistry(this.sqlRegistry);
		this.basicSqlService.loadSql();
		
//		this.oxmSqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
//		basicSqlService에게 위임
		return this.basicSqlService.getSql(key);
		//직접 getSql 메소드 실행
//		try{
//			return this.sqlRegistry.findSql(key);
//		}catch(SqlNotFoundException e){
//			throw new SqlRetrievalFailureException(e);
//		}
	}
	
	/**
	 * OxmSqlService 에서만 사용
	 * @author 최병철
	 *
	 */
	private class OxmSqlReader implements SqlReader{
		//setter는 outter 클래스에
		private Unmarshaller unmarshaller;
//		private static final String DEFAULT_SQLMAP_FILE_PATH = "sqlmap.xml";
		private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);
		
		//외부에서 수동으로 Di 받음
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}

		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}

		@Override
		public void read(SqlRegistry registry) {
			// TODO Auto-generated method stub
			try{
//				Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFilePath));
				Source source = new StreamSource(sqlmap.getInputStream());
				Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(source);

				for(SqlType sql : sqlmap.getSql()){
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			}catch(IOException e){
				throw new IllegalArgumentException(this.sqlmap.getFilename()+"을 가져올 수 없습니다",e);
			}
		}
		
	}
}
