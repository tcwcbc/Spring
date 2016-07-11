package user.dao;

import java.util.List;

import javax.sql.DataSource;

import user.domain.User;

public interface UserDao {
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	void setDataSource(DataSource dataSource);
	void update(User user1);
}
