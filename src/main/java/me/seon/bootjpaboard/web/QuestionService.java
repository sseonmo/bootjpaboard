package me.seon.bootjpaboard.web;

import com.querydsl.jpa.JPQLQuery;
import lombok.AllArgsConstructor;
import me.seon.bootjpaboard.domain.QQuestion;
import me.seon.bootjpaboard.domain.Question;
import me.seon.bootjpaboard.domain.QuestionRepository;
import me.seon.bootjpaboard.domain.QuestionSearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class QuestionService extends QuerydslRepositorySupport  {

	@Resource
	private QuestionRepository repository;

	public QuestionService() {
		super(Question.class);
	}

	public Page<Question> findAll(final QuestionSearchType type, final String value, final Pageable pageable) {

		final QQuestion question = QQuestion.question;
		final JPQLQuery<Question> query;

		switch (type) {

			case TITLE:
				query = from(question)
						.where(question.title.likeIgnoreCase(value+"%"));
				break;

			case USERNM:
				query = from(question)
						.where(question.writer.userId.likeIgnoreCase(value+"%"));
				break;

			case ALL:
				query = from(question).fetchAll();
				break;

			default:
				throw  new IllegalArgumentException();

		}

		final List<Question> questions = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(questions, pageable, query.fetchCount());
	}
}
