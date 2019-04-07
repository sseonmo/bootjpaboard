package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class WelcomeController {

	Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@Resource
	private QuestionRepository repository;

	@GetMapping("/hello")
	public String welcome(Model model, String name,int age) {
		logger.debug("name : {}, age : {}", name, age);
		model.addAttribute("name", name);
		model.addAttribute("age", age);
		return "welcome";
	}

	@GetMapping("")
	public String index(Model model) {
		model.addAttribute("questions", repository.findAll());
		return "/index";
	}

}
