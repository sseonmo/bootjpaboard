package me.seon.bootjpaboard.web;

import lombok.RequiredArgsConstructor;
import me.seon.bootjpaboard.domain.*;
import me.seon.bootjpaboard.util.HttpSessionUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class AnswerService {

	private final AnswerResoritory answerResoritory;

	private final QuestionRepository questionRepository;

	public Answer create(Long questionId, AnswerDto.CreateReq createReq, HttpSession session) {

		Question question = questionRepository.findById(questionId).orElseThrow(IllegalArgumentException::new);

		Answer answer = Answer.builder()
				.writer(HttpSessionUtil.getUserFormSession(session))
				.question(question)
				.contents(createReq.getContents())
				.build();

		return answerResoritory.save(answer);
	}

	public Result delete(Answer answer) {
		answerResoritory.delete(answer);
		return Result.ok();
	}
}
