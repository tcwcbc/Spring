package user.dao;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import user.sqlservice.jaxb.SqlType;
import user.sqlservice.jaxb.Sqlmap;

/**
 * SqlReader 구현 클래스
 * @author 최병철
 *
 */
public class JaxbSqlReader implements SqlReader {

	//sqlmap.xml의 경로를 디폴트 값으로 초기화 한 후, 원한다면 setter로 수정
	private static final String DEFAULT_SQLMAP_FILE_PATH = "sqlmap.xml";
	
	private String sqlmapFilePath = DEFAULT_SQLMAP_FILE_PATH;
	
	
	public void setSqlmapFilePath(String sqlmapFilePath) {
		this.sqlmapFilePath = sqlmapFilePath;
	}


	
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
