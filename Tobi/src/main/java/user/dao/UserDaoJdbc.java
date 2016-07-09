package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import user.domain.User;


public class UserDaoJdbc implements UserDao {
//	private DataSource dataSource;
	//직접 JDBCContext를 구현한 것 사용
//	private JdbcContext jdbcContext;
	//제공되는 메소드 사용
	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> rowMapper = new RowMapper<User>(){
		@Override
		public User mapRow(ResultSet arg0, int arg1) throws SQLException {
			// TODO Auto-generated method stub
			User user = new User();
			user.setId(arg0.getString("id"));
			user.setName(arg0.getString("name"));
			user.setPassword(arg0.getString("password"));
			return user;
		}		};

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
//		this.jdbcContext = new JdbcContext();
//		this.jdbcContext.setDataSource(dataSource);
		
//		this.dataSource = dataSource;
	}
	

	//예외 처리는 jdbcTemplete에서 처리.
	public void add(final User user) {
		
		//콜백 템플릿 구조 사용
//		this.jdbcContext.workWithStatementStrategy(
//				new StatementStrategy() {			
//					public PreparedStatement makePreparedStatement(Connection c)
//					throws SQLException {
//						PreparedStatement ps = 
//							c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
//						ps.setString(1, user.getId());
//						ps.setString(2, user.getName());
//						ps.setString(3, user.getPassword());
//						
//						return ps;
//					}
//				}
//		);
		
		//오버로딩 된 메소드 사용
		this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)"
				, user.getId(), user.getName(), user.getPassword());
	}

	//예외 처리는 jdbcTemplete에서 처리.
	public User get(String id){
		//로우매퍼를 사용한 rs로 객체를 만들어 반환하는 메소드
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id}, this.rowMapper);
		
		/*Connection c = this.dataSource.getConnection();
		PreparedStatement ps = c
				.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();

		User user = null;
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}

		rs.close();
		ps.close();
		c.close();
		
		if (user == null) throw new EmptyResultDataAccessException(1);

		return user;*/
	}
	
	//예외 처리는 jdbcTemplete에서 처리.
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", 
				this.rowMapper);
		
	}

	//예외 처리는 jdbcTemplete에서 처리.
	public void deleteAll() {
		//사용자 정의 메소드
//		this.jdbcContext.excuteSql("delete from users");
		//제공되는 오버로딩 된 메소드
//		this.jdbcTemplate.update("delete from users");
		//콜백 템플릿
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				return con.prepareStatement("delete from users");
			}
		});
	}
	//예외 처리는 jdbcTemplete에서 처리.
	public int getCount() {
		
		//템플릿. 콜백2
		return this.jdbcTemplate.query(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection arg0) throws SQLException {
				// TODO Auto-generated method stub
				return arg0.prepareStatement("select count(*) from users");
			}
		}, new ResultSetExtractor<Integer>() {
			
			//query메소드의 결과도 제너릭타입을 따라감
			@Override
			public Integer extractData(ResultSet arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				arg0.next();
				return arg0.getInt(1);
			}
		});
		
		//오버로딩 된 메소드
//		return this.jdbcTemplate.queryForInt("select count(*) from users");
		
		//사용자 정의
		/*Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("select count(*) from users");
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		ps.close();
		c.close();
		return count;*/
	}
}
