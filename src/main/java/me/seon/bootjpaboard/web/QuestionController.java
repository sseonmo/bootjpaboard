package me.seon.bootjpaboard.web;

import lombok.RequiredArgsConstructor;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.Result;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Controller
@Transactional
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
	final Logger logger = LoggerFactory.getLogger(QuestionController.class);

	private final QuestionRepository repository;

	private boolean hasPermission(HttpSession session, Question question) {
		if (!HttpSessionUtil.isLoginUser(session))
			throw new IllegalStateException("로그인이 필요합니다.");

		User userFormSession = HttpSessionUtil.getUserFormSession(session);
		if(!question.isEqualsWriter(userFormSession))
			throw new IllegalStateException("자신의 글만 수정, 삭제 가능합니다.");

		return true;
	}

	private Result vaild(HttpSession session, Question question) {

		if (!HttpSessionUtil.isLoginUser(session))
			return Result.fail("로그인이 필요합니다.");

		User userFormSession = HttpSessionUtil.getUserFormSession(session);
		if(!question.isEqualsWriter(userFormSession))
			return Result.fail("자신의 글만 수정, 삭제 가능합니다.");

		return Result.ok();
	}

	@GetMapping("/form")
	public String form(HttpSession session) {
		if (!HttpSessionUtil.isLoginUser(session))
			return "/user/login";

		return "/qna/form";
	}

	@PostMapping("")
	public String create(String title, String contents, HttpSession session) {
		logger.debug("question create : [{}] [{}]", title, contents);
		if (!HttpSessionUtil.isLoginUser(session))
			return "/user/login";

		User userFormSession = HttpSessionUtil.getUserFormSession(session);
		repository.save(new Question(userFormSession,title, contents));
		return "redirect:/";
 	}

 	@GetMapping("/{id}")
	public String Show(@PathVariable("id") Question question, Model model) {
		logger.info("question show : [{}]", question.toString());
		model.addAttribute("question", question);

		return "/qna/show";
	}

	@GetMapping("/{id}/form")
	public String updateform(@PathVariable("id") Question question, Model model, HttpSession session) {
		Result result = vaild(session, question);

		if (result.isVaild()) {
			model.addAttribute("question", question);
			return "/qna/updateForm";
		}

		model.addAttribute("errorMessage",result.getErrorMessage());
		return "/user/login";
	}

	@PutMapping("/{id}")
	public String update(@PathVariable("id") Question question,  String title, String contents, HttpSession session, Model model){
		logger.info("update update : [{}] / [{}] / [{}]", question.toString(), title, contents  );

		Result result = vaild(session, question);

		if (result.isVaild()) {
			question.update(title, contents);
			return String.format("redirect:/questions/%d", question.getId());
		}

		model.addAttribute("errorMessage",result.getErrorMessage());
		return "/user/login";

	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Question question, HttpSession session, Model model) {
		Result result = vaild(session, question);

		if (result.isVaild()) {
//			repository.deleteById(question.getId());
			question.delete();
			return "redirect:/";
		}

		model.addAttribute("errorMessage",result.getErrorMessage());
		return "/user/login";

		/*
		try {
			hasPermission(session, question);
			repository.deleteById(question.getId());
			return "redirect:/";
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage",e.getMessage());
			return "/user/login";
		}
		*/
	}


}