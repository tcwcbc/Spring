package com.spring.mvc.view;

/**
 * Marshalling을 통해 OXM해주기 위한 모델 객체
 * @author 최병철
 *
 */
public class Info {
	String message;

	public Info(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
