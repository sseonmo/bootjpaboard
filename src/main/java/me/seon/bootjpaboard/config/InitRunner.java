package me.seon.bootjpaboard.config;

import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import me.seon.bootjpaboard.domain.model.Email;
import me.seon.bootjpaboard.domain.model.Password;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitRunner implements ApplicationRunner {

	@Resource
	UserRepository repository;

	@Resource
	QuestionRepository questionRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		Email email = Email.builder().value("seonmo@gmila.com").build();
		Password password = Password.builder().value("pass").build();

		User user = User.builder().userId("seonmo")
				.password(password)
				.name("선모")
				.email(email)
				.build();

//		User user = new User();
//		user.setUserId("seonmo");
//		user.setEmail("seonmo@gmail.com");
//		user.setPassword("pass");
//		user.setName("선모");
//		user.setCreateDate(LocalDateTime.now());

		repository.save(user);

		Question question = new Question(user, "testTitle", "testContent");
		questionRepository.save(question);

	}
}
