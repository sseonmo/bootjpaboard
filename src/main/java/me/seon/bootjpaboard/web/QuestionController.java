package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	final Logger logger = LoggerFactory.getLogger(QuestionController.class);

	@Resource
	private QuestionRepository repository;

	@GetMapping("/form")
	public String form(HttpSession session) {
		if (!HttpSessionUtil.isLoginUser(session))
			return "/user/loginForm";
		return "/qna/form";
	}

	@PostMapping("")
	public String create(String title, String contents, HttpSession session) {
		logger.debug("question create : [{}] [{}]", title, contents);
		if (!HttpSessionUtil.isLoginUser(session))
			return "/user/loginForm";

		User userFormSession = HttpSessionUtil.getUserFormSession(session);
		repository.save(new Question(userFormSession.getUserId(),title, contents));
		return "redirect:/";
 	}

}