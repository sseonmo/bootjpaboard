package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

	final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserRepository repository;

	@GetMapping("/loginForm")
	public String goLogin() {
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
	public String updateForm(@PathVariable Long id, Model model, HttpSession session) throws Exception {
		logger.debug("updateForm : parameter : [{}]", id);

		Object tempUser = session.getAttribute("sessionUser");
		if(tempUser == null) return "redirect:/user/form";

		User sessionUser = (User) tempUser;
		if (!id.equals(sessionUser.getId())) {
			throw new IllegalStateException("you can't update the anther user.");
		}

		Optional<User> byId = repository.findById(sessionUser.getId());
		System.out.println(byId.get().toString());
		model.addAttribute("user", byId.orElseThrow(Exception::new));
		return "/user/updateForm";
	}

	@PostMapping("/login")
	public String login(User user, HttpSession session, Model model) {

		Optional<User> byUserId = repository.findByUserId(user.getUserId());
		if (byUserId.isPresent()) {
			if (byUserId.get().getPassword().equals(user.getPassword())) {
				session.setAttribute("sessionUser", byUserId.get());
				return "redirect:/";
			}
		}

		//실패
		model.addAttribute("failure",true);
		return  "/user/login";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("sessionUser");
		return "redirect:/";
	}

	@PostMapping("/create")
	public String create(User user) {
		logger.debug(user.toString());
		repository.save(user);
		return "redirect:/user/list";
	}

	@PutMapping("/{id}")
	public String update(@PathVariable Long id, User updatedUser, HttpSession session) {
		logger.debug("update {} {}", id, updatedUser.toString());
		Object tempUser = session.getAttribute("sessionUser");

		if(tempUser == null)	return "redirect:/user/loginForm";

		User sessionUser = (User) tempUser;
		if(!id.equals(sessionUser.getId()))
			throw new IllegalStateException("you can't update the anther user.");

		repository.save(updatedUser);

		session.setAttribute("sessionUser", updatedUser);
		return "redirect:/user/list";
	}

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("users", repository.findAll());
		return "/user/list";
	}
}
