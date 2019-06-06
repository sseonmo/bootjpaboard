package me.seon.bootjpaboard.web;

import lombok.AllArgsConstructor;
import me.seon.bootjpaboard.domain.PageRequest;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionSearchType;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionService service;

	@GetMapping("")
	public Page<Question> findAll(
			@RequestParam(name = "type") final QuestionSearchType type,
			@RequestParam(name = "value", required = false) final String value,
			final PageRequest pageRequest) {

		return service.findAll(type, value, pageRequest.of());
	}
}
