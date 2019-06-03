package me.seon.bootjpaboard.web;

import lombok.AllArgsConstructor;
import me.seon.bootjpaboard.domain.PageRequest;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionSearchType;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionService service;

	@GetMapping("")
	public Page<Question> findAll(
			@PathVariable(name = "type") final QuestionSearchType type,
			@PathVariable(name = "value", required = false) final String value,
			final PageRequest pageRequest) {

		return service.findAll(type, value, pageRequest.of());
	}
}
