package me.seon.bootjpaboard.config;

import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class InitRunner implements ApplicationRunner {

	@Resource
	UserRepository repository;

	@Resource
	QuestionRepository questionRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = User.builder().userId("seonmo")
				.password("pass")
				.name("선모")
				.email("seonmo@gmila.com")
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
