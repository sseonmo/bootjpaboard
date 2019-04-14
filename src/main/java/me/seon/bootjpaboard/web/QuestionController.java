package me.seon.bootjpaboard.web;

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
import java.util.Optional;

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
	public String updateform(@PathVariable("id") Question question, Model model) {
		model.addAttribute("question", question);
		return "/qna/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@PathVariable("id") Long id,  String title, String contents){
		logger.info("update update : [{}] / [{}] / [{}]", id, title, contents );
		Optional<Question> byId = repository.findById(id);
		byId.ifPresent( qu -> {
			qu.update(title, contents);
			repository.save(qu);
		});
		return String.format("redirect:/questions/%d", id);
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		repository.deleteById(id);
		return "redirect:/";
	}


}