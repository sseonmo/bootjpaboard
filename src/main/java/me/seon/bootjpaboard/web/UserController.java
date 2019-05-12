package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.AccountDto;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import me.seon.bootjpaboard.exception.AccountNotFountException;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@Transactional
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserRepository repository;

	@Resource
	private UserService userService;

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

		if(!HttpSessionUtil.isLoginUser(session))
			return "redirect:/user/form";

		User sessionUser = HttpSessionUtil.getUserFormSession(session);

		if (!sessionUser.matchId(id)) {
			throw new IllegalStateException("you can't update the anther user.");
		}

		model.addAttribute("user", userService.findById(sessionUser.getId()));
		return "/user/updateForm";
	}


	@PostMapping("/login")
	public String login(@Valid final AccountDto.LoginReq loginDto , HttpSession session, Model model) {

		User loginUser = userService.findByUserId(loginDto);

		if (loginDto.getPassword().isMatched(loginUser.getPassword().getValue())) {
			HttpSessionUtil.setSession(session, loginUser);
			return "redirect:/";
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
	public String create(@Valid final AccountDto.SignUpReq user) {
		logger.debug("create User : [{}]", user.toString());

		userService.create(user);

		return "redirect:/user/list";

	}

	@PutMapping("/{id}")
	public String update(@PathVariable final Long id, final User updatedUser, HttpSession session) {
		logger.debug("update {} {}", id, updatedUser.toString());

		if(!HttpSessionUtil.isLoginUser(session))	return "redirect:/user/loginForm";
		User sessionUser = HttpSessionUtil.getUserFormSession(session);

		if(!sessionUser.matchId(id))
			throw new IllegalStateException("you can't update the anther user.");

		User user;

		try {
			user = userService.findById(id);

		} catch (AccountNotFountException ane) {
			return "redirect:/user/loginForm";
		}

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
