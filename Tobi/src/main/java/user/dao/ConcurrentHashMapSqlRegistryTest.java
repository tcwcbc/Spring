package user.dao;

import user.dao.concurrent.AbstractUpdatableSqlRegistryTest;
import user.dao.concurrent.ConcurrentHashMapSqlRegistry;
/**
 * Updatable 인터페이스를 구현한 클래스들 중 ConcurrentHashmap을 사용한 클래스를 테스트
 * @author 최병철
 *
 */
public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		// TODO Auto-generated method stub
		return new ConcurrentHashMapSqlRegistry();
	}

}
