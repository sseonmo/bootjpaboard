package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.Answer;
import me.seon.bootjpaboard.domain.AnswerResoritory;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/questions/{question}/answer")
public class ApiAnswerController {

	static final Logger logger = LoggerFactory.getLogger(ApiAnswerController.class);

	@Resource
	private AnswerResoritory resoritory;

	@PostMapping("")
	public Answer create(@PathVariable("question") Question question, String contents, HttpSession session) {
		logger.info("create question : [{}] / contents : [{}]", question, contents);

		if(!HttpSessionUtil.isLoginUser(session)) return null;

		User user = HttpSessionUtil.getUserFormSession(session);
		Answer answer = new Answer(user, question, contents);
		Answer save = resoritory.save(answer);

		return save;

	}



}
