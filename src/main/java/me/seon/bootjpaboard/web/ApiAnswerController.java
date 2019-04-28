package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.*;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
		return resoritory.save(answer);

	}

	@DeleteMapping("/{id}")
	public Result delete(@PathVariable("id") Answer answer, HttpSession session) {
		logger.info("delete answer : [{}]", answer);

		if(!HttpSessionUtil.isLoginUser(session)) return Result.fail("로그인해야 합니다.");

		User loginUser = HttpSessionUtil.getUserFormSession(session);

		if(!answer.isEqualsWriter(loginUser))
				return Result.fail("자신의 글만 삭제할 수 있습니다.");

		resoritory.delete(answer);
		return Result.ok();

	}



}
