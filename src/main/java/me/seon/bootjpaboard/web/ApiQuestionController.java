package me.seon.bootjpaboard.web;

import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/question")
public class ApiQuestionController {

	@Resource
	private QuestionRepository repository;

	@GetMapping("")
	public Page<Question> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
