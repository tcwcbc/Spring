package user.domain;

public class User {
	String id;
	String name;
	String password;
	//사용자 등급
	Level level;
	//로그인 횟수, 이 값이 50이상일 경우 SILVER로 등급 업
	int login;
	//추천 횟수, 이 값이 30 이상이면 GOLE로 등급 업
	int recommand;
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommand() {
		return recommand;
	}

	public void setRecommand(int recommand) {
		this.recommand = recommand;
	}

	public User() {
	}
	
	public User(String id, String name, String password, Level level, int login, int recommand) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommand = recommand;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * 서비스에서 하나하나 업그레이드를 해주기 보다는 
	 * user 클래스에서 자체적으로 업그레이드 함으로써
	 * 객체단위로 업그레이드 수행
	 */
	public void upgradeLevel(){
		Level nextLevel = this.level.getNextLevel();
		if(nextLevel==null){
			throw new IllegalStateException(this.level+"은 업그레이드가 불가합니다.");
		}else{
			this.level = nextLevel;
		}
	}
}
