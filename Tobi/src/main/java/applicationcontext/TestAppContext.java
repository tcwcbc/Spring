package applicationcontext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

import user.dao.UserDao;
import user.service.DummyMailSender;
import user.service.UserService;
import user.service.UserServiceTest.TestUserService;

