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
		Question question1 = new Question(user, "testTitle1", "testContent1");
		Question question2 = new Question(user, "testTitle2", "testContent2");
		Question question3 = new Question(user, "testTitle3", "testContent3");
		Question question4 = new Question(user, "testTitle4", "testContent4");
		Question question5 = new Question(user, "testTitle5", "testContent5");
		Question question6 = new Question(user, "testTitle6", "testContent6");
		Question question7 = new Question(user, "testTitle7", "testContent7");
		Question question8 = new Question(user, "testTitle8", "testContent8");
		Question question9 = new Question(user, "testTitle9", "testContent9");
		Question question10 = new Question(user, "testTitle10", "testContent10");
		Question question11 = new Question(user, "testTitle11", "testContent11");
		questionRepository.save(question);
		questionRepository.save(question1);
		questionRepository.save(question2);
		questionRepository.save(question3);
		questionRepository.save(question4);
		questionRepository.save(question5);
		questionRepository.save(question6);
		questionRepository.save(question7);
		questionRepository.save(question8);
		questionRepository.save(question9);
		questionRepository.save(question10);
		questionRepository.save(question11);

	}
}
