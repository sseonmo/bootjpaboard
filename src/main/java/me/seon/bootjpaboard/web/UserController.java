package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@Transactional
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

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

		if(!HttpSessionUtil.isLoginUser(session)) return "redirect:/user/form";
		User sessionUser = HttpSessionUtil.getUserFormSession(session);

		if (!sessionUser.matchId(id)) {
			throw new IllegalStateException("you can't update the anther user.");
		}

		Optional<User> byId = repository.findById(sessionUser.getId());
		model.addAttribute("user", byId.orElseThrow(Exception::new));
		return "/user/updateForm";
	}

	@PostMapping("/login")
	public String login(User user, HttpSession session, Model model) {

		Optional<User> byUserId = repository.findByUserId(user.getUserId());
		if (byUserId.isPresent()) {
			if(user.matchPassword(byUserId.get().getPassword())){
				HttpSessionUtil.setSession(session, byUserId.get());
				return "redirect:/";
			}
		}
		//실패
		model.addAttribute("failure",true);
		return  "/user/login";
	}

	@GetMapping("/login/{id}")
	public void login(@PathVariable("id") User user) {
		logger.info("/login/{id}   [{}]", user);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		HttpSessionUtil.deleteSession(session);
		return "redirect:/";
	}

	@PostMapping("/create")
	public String create(User user) {
		logger.debug(user.toString());
		repository.save(user);
		return "redirect:/user/list";
	}

	@PutMapping("/{id}")
	public String update(@PathVariable final Long id, final User updatedUser, HttpSession session) {
		logger.debug("update {} {}", id, updatedUser.toString());

		if(!HttpSessionUtil.isLoginUser(session))	return "redirect:/user/loginForm";
		User sessionUser = HttpSessionUtil.getUserFormSession(session);

		if(!sessionUser.matchId(id))
			throw new IllegalStateException("you can't update the anther user.");

		User user = repository.findById(id).orElse(null);

		if(user == null ) return "redirect:/user/loginForm";

		user.updateAccout(updatedUser);
//		repository.save(user);
		HttpSessionUtil.setSession(session, user);
		return "redirect:/user/list";
	}

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("users", repository.findAll());
		return "/user/list";
	}
}
