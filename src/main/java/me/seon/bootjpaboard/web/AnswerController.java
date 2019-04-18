package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.Answer;
import me.seon.bootjpaboard.domain.AnswerResoritory;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions/{question}/answer")
public class AnswerController {

	static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

	@Resource
	private AnswerResoritory resoritory;

	@PostMapping("")
	public String create(@PathVariable("question") Question question, String contents, HttpSession session) {
		logger.info("create question : [{}] / contents : [{}]", question, contents);

		if(!HttpSessionUtil.isLoginUser(session)) return "/user/login";

		User user = HttpSessionUtil.getUserFormSession(session);
		Answer answer = new Answer(user, question, contents);
		resoritory.save(answer);
//		question.getAnswers().add(answer);
		return String.format("redirect:/questions/%d", question.getId());

	}



}
