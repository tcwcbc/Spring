package com.spring.mvc.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import com.spring.mvc.AbstractDispatcherServletTest;

/**
 * ���� ���� Ŭ������ �������Ϸ� ���� web.xml�� ������� �ʰ� MVC�� ��Ʈ�ѷ��� ����Ͽ� ������ ���. �̷��� �ϸ�
 * Ŀ���͸���¡�� ����������. ������, init()�̳� destroy()�� ���� �����ֱ� �޼ҵ带 ������ �����������.
 * 
 * Spring MVC�� �����ϴ� ��Ʈ�ѷ� 4����
 * 1. Servlet Ÿ��		: MVC�� �𸣴� ǥ�� servlet Ÿ���̱� ������ �𵨰� �並 ��ȯX
 * 2. HttpRequest Ÿ��	: ����Ÿ�԰� ����, Remote Method Invocation�� ���� �ο췹�� ��ɻ���ϱ� ����
 * 3. Controller Ÿ��	: ������̼���Ʈ�ѷ� ������ ��ǥ���� ��Ʈ�ѷ�����. 
 * 						�������̽� Controller -> ����Ŭ���� AbstractController -> ��� MyController
 * 4. Annotated Ÿ��		: ��Ʈ�ѷ� Ŭ������ �޼ҵ忡 ���� �ֳ����̼ǰ� ������ �ľ��Ͽ� ����
 * 
 * SimpleServletHandlerAdapter�� ������ ������ �ڵ鷯����ʹ� ����Ʈ �������� ����� �Ǿ� �����Ƿ� ���� ������� �ʿ� X
 * @author �ֺ�ö
 *
 */
public class ServletControllerTest extends AbstractDispatcherServletTest {

	@Test
	public void helloServletController() throws UnsupportedEncodingException, ServletException, IOException {
		//�ڵ鷯����Ϳ� ������Ʈ�ѷ��� �ڹ��ڵ带 �̿��غ����� ���
		setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class);
		
		initRequest("/hello").addParameter("name", "Spring");

		assertThat(runService().getContentAsString(), is("Hello Spring"));
	}

	@Named("/hello")
	static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			// Get �޼ҵ� name �Ķ���� ���� res�� ��´�.
			String name = req.getParameter("name");
			resp.getWriter().print("Hello " + name);
		}
	}
}
