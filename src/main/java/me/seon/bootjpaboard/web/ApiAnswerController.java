package me.seon.bootjpaboard.web;

import lombok.RequiredArgsConstructor;
import me.seon.bootjpaboard.domain.*;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{question}/answer")
@RequiredArgsConstructor
public class ApiAnswerController {

	static final Logger logger = LoggerFactory.getLogger(ApiAnswerController.class);

	private final AnswerResoritory resoritory;

	private final AnswerService service;

	@PostMapping("")
	public AnswerDto.Res create(@PathVariable("question") Long questionId, @RequestBody @Valid final AnswerDto.CreateReq createReq, HttpSession session) {
		logger.info("create question : [{}] / contents : [{}]", questionId, createReq);

		if(!HttpSessionUtil.isLoginUser(session)) return null;

		return new AnswerDto.Res(service.create(questionId, createReq, session));

	}


	@DeleteMapping("/{id}")
	public Result delete(@PathVariable("id") Answer answer, HttpSession session) {
		logger.info("delete answer : [{}]", answer);

		if(!HttpSessionUtil.isLoginUser(session)) return Result.fail("로그인해야 합니다.");

		User loginUser = HttpSessionUtil.getUserFormSession(session);

		if(!answer.isEqualsWriter(loginUser))
				return Result.fail("자신의 글만 삭제할 수 있습니다.");

		return service.delete(answer);
	}



}
