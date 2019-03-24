package me.seon.bootjpaboard.web;

import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
	final Logger logger = LoggerFactory.getLogger(UserController.class);

	private List<User> users = new ArrayList<>();

	@PostMapping("/create")
	public String create(User user) {
		logger.debug(user.toString());
		users.add(user);
		return "redirect:/list";
	}

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("users", users);
		return "list";
	}
}
