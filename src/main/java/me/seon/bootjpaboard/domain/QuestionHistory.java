package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seon.bootjpaboard.domain.model.DateTime;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private DateTime dateTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private Status status;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false, updatable = false)
	private Question question;

	@Builder
	public QuestionHistory(Status status, Question question) {
		this.status = status;
		this.question = question;
	}
}
