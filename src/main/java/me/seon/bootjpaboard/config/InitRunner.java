package me.seon.bootjpaboard.config;

import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitRunner implements ApplicationRunner {

	@Resource
	UserRepository repository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = new User();
		user.setUserId("seonmo");
		user.setEmail("seonmo@gmail.com");
		user.setPassword("password");
		user.setName("선모");

		repository.save(user);

	}
}