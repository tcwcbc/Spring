package user.dao;

/**
 * 확장이필요할 가능성이 적을 경우 의존 object를 직접 DI
 * 이 경우 JaxbSqlReader에 sqlpam.xml의 경로를 지정해줘야 하는데
 * 설정파일에서 DI해서 이 클래스에서 지정하는 것보단 JaxbSqlReader 안에서
 * 디폴트값을 지정해주는 방법을 사용
 * @author 최병철
 *
 */
public class DefaultSqlService extends BasicSqlService{
	public DefaultSqlService() {
		// TODO Auto-generated constructor stub
		setSqlReader(new JaxbSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}
