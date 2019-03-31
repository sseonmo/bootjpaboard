package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
	final Logger logger = LoggerFactory.getLogger(UserController.class);

	private List<User> users = new ArrayList<>();

	@Autowired
	private UserRepository repository;

	@GetMapping("/login")
	public String goLogin(User user) {
		return "/user/login";
	}

	@GetMapping("/profile")
	public String goProfile() {
		return "/user/profile";
	}

	@GetMapping("/form")
	public String goForm() {
		return "/user/form";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@PathVariable Long id, Model model) throws Exception {
		logger.debug("updateForm : parameter : [{}]", id);
		Optional<User> byId = repository.findById(id);
		System.out.println(byId.get().toString());
		model.addAttribute("user", byId.orElseThrow(Exception::new));
		return "/user/updateForm";
	}

	@PostMapping("login")
	public String login(User user) {
		final String[] returnUrl = new String[1];
		returnUrl[0] = "login_failed";

		Optional<User> byUserId = repository.findByUserId(user.getUserId());
		byUserId.ifPresent(u -> {
					if (u.getPassword().equals(user.getPassword()) )
						returnUrl[0] = "success";
				}
		);

		return returnUrl[0];
	}

	@PostMapping("/create")
	public String create(User user) {
		logger.debug(user.toString());
		repository.save(user);
		return "redirect:/user/list";
	}

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("users", repository.findAll());
		return "/user/list";
	}
}
