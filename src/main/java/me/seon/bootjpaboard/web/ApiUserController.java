package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

	@Resource
	private UserRepository userRepository;

	@GetMapping("/{id}")
	public User show(@PathVariable("id") User user) {
		return user;
	}
}
