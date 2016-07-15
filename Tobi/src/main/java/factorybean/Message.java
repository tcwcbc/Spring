package factorybean;

/**
 * 스프링 빈으로 등록하여 사용하면 private를 무시하고 객체를 생성할 수 있으나
 * 원래 생성자를 private으로 만든 클래스는 그런 방식으로 사용하는 것이 위험.
 * @author 최병철
 *
 */
public class Message {
	String text;
	
	//생성자 private
	private Message(String text) {
		this.text = text;
		// TODO Auto-generated constructor stub
	}
	
	public String getText(){
		return this.text;
	}
	
	//이 스태틱 메소드를 사용하여 객체 생성
	public static Message newMessage(String text){
		return new Message(text);
	}
}
