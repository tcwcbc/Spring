package user.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mysql.jdbc.Connection;

import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

/**
 * 비지니스 로직을 담고있는 클래스
 * DAO는 순수하게 DB접근을 위한 행동을 하고
 * 비지니스 로직은 여기서 구현ㄴ
 * @author 최병철
 *
 */
public class UserServiceImpl implements UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER=50;
	public static final int MIN_RECOMMANDCOUNT_FOR_GOLD=30;
	
	//회원등급 업그레이드 정책을 달리하기 위한 변수
//	UserLevelUpgradePolicy policy;
//	public void setPolicy(UserLevelUpgradePolicy policy){
//		this.policy = policy;
//	}
	
	
	UserDao userDao;
	/**
	 * xml로 DI
	 * @param userDao
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
//	private DataSource dataSource;
//	
//	public void setdataSource(DataSource dataSource){
//		this.dataSource = dataSource;
//	}
	
	MailSender mailSender;
	
	public void setMailSender(MailSender mailSender){
		this.mailSender = mailSender;
	}
	/**
	 * 일괄적으로 모든 회원의 등급을 갱신하는 메소드
	 */
	public void upgradeLevels() {
		
		// 로컬 트랜잭션 매니저
		// 트렌잭션 동기화 및 동기화 저장소에 저장
		// 이후 DAO 작업은 이트렌잭션 동기화 저장소에 있는 커넥션을 가지고 작업함.
//		TransactionSynchronizationManager.initSynchronization();
//		Connection c = (Connection) DataSourceUtils.getConnection(dataSource);
//		c.setAutoCommit(false);
		
		// 글로벌 트랜잭션
		// 하이버네이트, JPA등의 여러개의 DB를 하나의 트랜잭션매니저로 관리
		
		//트랜잭션 경계설정
		List<User> users = userDao.getAll();
		for(User user : users){
			if(canUpgradeLevel(user)){
				upgradeLevel(user);
//				userDao.update(user);
			}
		}
			//자원을 반납하고 커넥션을 해제 및 초기화
//			DataSourceUtils.releaseConnection(c, dataSource);
//			TransactionSynchronizationManager.unbindResource(this.dataSource);
//			TransactionSynchronizationManager.clearSynchronization();
	}
	
	/**
	 * 회원 등급 업그레이드 가능여부를 현재 등급에 따라 판단
	 * @param user
	 * @return	회원 업그레이드 가능여부
	 */
	public boolean canUpgradeLevel(User user){
		Level currentLevel =user.getLevel(); 
		switch(currentLevel){
			case BASIC : return (user.getLogin()>=MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommand()>=MIN_RECOMMANDCOUNT_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Level : "+currentLevel);
		}
	}
	

	/**
	 * 현재 등급에 따라 업그레이드를 하고 회원 등급 갱신
	 * @param user
	 */
	public void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUogradeEMail(user);
	}


	/**
	 * 회원 등급이 갱신되었을 때 메일을 발송하는 메소드
	 * @param user
	 */
	private void sendUogradeEMail(User user) {
		// TODO Auto-generated method stub
		//JavaMail을 추상화한 핵심인터페이스를 구현한 클래스 사용
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setFrom("useradmin@ksug.org");
		message.setSubject("Upgrade 안내");
		message.setText("사용자님의 등급이 "+user.getLevel().name()+"로 업그레이드 되었습니다.");

		mailSender.send(message);
		
		/*
		 * 일반적인 javaMail을 이용
		 * Properties prop = new Properties();
		prop.put("mail.smtp.host", "mail.ksug.org");
		Session s = Session.getInstance(prop, null);
		
		MimeMessage message = new MimeMessage(s);
	
		try {
			message.setFrom(new InternetAddress("useradmin@ksug.org"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("Upgrade 안내");
			message.setText("사용자님의 등급이 "+user.getLevel().name()+"로 업그레이드 되었습니다.");
			
			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}*/
	}
	/**
	 * 새로운 회원이 가입할때 등급 기본값을 BASIC으로 세팅
	 * @param user
	 */
	
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
}
