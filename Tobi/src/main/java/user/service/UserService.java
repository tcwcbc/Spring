package user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.domain.User;

@Transactional
public interface UserService {
	@Transactional(readOnly=true)
	User get(String id);
	@Transactional(readOnly=true)
	List<User> getAll();
	void deleteAll();
	void update(User user);
	void add(User user);
	void upgradeLevels();
}
