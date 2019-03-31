package me.seon.bootjpaboard.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@GetMapping("/hello")
	public String welcome(Model model, String name,int age) {
		logger.debug("name : {}, age : {}", name, age);
		model.addAttribute("name", name);
		model.addAttribute("age", age);
		return "welcome";
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

}
