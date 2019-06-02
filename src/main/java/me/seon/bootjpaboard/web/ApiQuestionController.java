package me.seon.bootjpaboard.web;

import lombok.AllArgsConstructor;
import me.seon.bootjpaboard.domain.PageRequest;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionRepository repository;

	@GetMapping("")
	public Page<Question> findAll(final PageRequest pageable) {
		return repository.findAll(pageable.of());
	}
}
