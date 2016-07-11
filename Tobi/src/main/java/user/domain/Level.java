package user.domain;

/**
 * 사용자 등급을 매기기 위한 Enum 클래스
 * int값을 사용하면 의미가 불분명하고
 * 상수로 정의하여 사용한다면 이상한 값이 들어갈 수 있기 때문에
 * 하나의 객체로 만들어 사용
 * @author 최병철
 *
 */
public enum Level {
	//DB에 저장될 숫자와 다음단계의 회원등급을 enum 클래스 안에서 정의
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER)  ;
	
	private final int value;
	private final Level nextLevel;
	
	Level(int value, Level nextLevel) {
		this.value = value;
		this.nextLevel = nextLevel;
	}
	
	public Level getNextLevel() {
		return nextLevel;
	}

	public int intValue(){
		return value;
	}
	
	public static Level valueOf(int value){
		switch (value) {
		case 1: return BASIC;
		case 2: return SILVER;
		case 3: return GOLD;
		default: throw new AssertionError("Unknown value : "+value);
		}
	}
}
