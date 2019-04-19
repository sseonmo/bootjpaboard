package me.seon.bootjpaboard.web;

import javafx.print.Collation;
import me.seon.bootjpaboard.domain.Answer;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	final Logger logger = LoggerFactory.getLogger(QuestionController.class);

	@Resource
	private QuestionRepository repository;

	private boolean hasPermission(HttpSession session, Question question) {
		if (!HttpSessionUtil.isLoginUser(session))
			throw new IllegalStateException("로그인이 필요합니다.");

		User userFormSession = HttpSessionUtil.getUserFormSession(session);
		if(!question.isEqualsWriter(userFormSession))
			throw new IllegalStateException("자신의 글만 수정, 삭제 가능합니다.");

		return true;
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
		try {
			hasPermission(session, question);
			return "/qna/updateForm";
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage",e.getMessage());
			return "/user/login";
		}
	}

	@PutMapping("/{id}")
	public String update(@PathVariable("id") Question question,  String title, String contents, HttpSession session, Model model){
		logger.info("update update : [{}] / [{}] / [{}]", question.toString(), title, contents  );

		try {
			hasPermission(session, question);
			question.update(title, contents);
			repository.save(question);
			return String.format("redirect:/questions/%d", question.getId());
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage",e.getMessage());
			return "/user/login";
		}

	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Question question, HttpSession session, Model model) {
		try {
			hasPermission(session, question);
			repository.deleteById(question.getId());
			return "redirect:/";
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage",e.getMessage());
			return "/user/login";
		}
	}


}