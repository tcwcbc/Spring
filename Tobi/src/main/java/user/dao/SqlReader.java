package user.dao;

/**
 * XML로부터 읽어온 SQL을 저장하는 인터페이스
 * SqlRegistry와 직접적으로 연관
 * SqlService를 거치지 않는다
 * @author 최병철
 *
 */
public interface SqlReader {
	void read(SqlRegistry registry);
}
