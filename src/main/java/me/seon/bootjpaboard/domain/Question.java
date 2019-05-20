package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
//재귀오류 해결
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
// jpa에서는 프록시 생성을 위해서 기본 생정자를 반드시 하나를 생성해야한다.
// 이때 접근권한이 protected 이면 충분하다.. 굳이 외부에 열어둘 필요가 없다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"answers"} )
public class Question extends AbstractEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
//	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@OneToMany(mappedBy = "question", orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("id DESC")
	private List<Answer> answers = new ArrayList<>();

	@NotEmpty
	@Column(nullable = false)
	private String title;

	@OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<QuestionHistory> histories = new ArrayList<>();

	@Column(nullable = false)
	private String delYn;

	@Lob
	@Column(nullable = false)
	private String contents;


	@Builder
	public Question(User writer, String title, String content) {
		this.writer = writer;
		this.title = title;
		this.contents = content;
		this.delYn = "N";
		addHistory(Status.CREATE);

	}

	public void update(String title, String contents) {
		this.title = title;
		this.contents = contents;
		addHistory(Status.UPDATE);
	}

	public void delete() {
		this.delYn = "Y";
		addHistory(Status.DELETE);
	}

	private void addHistory(Status status) {
		this.histories.add(buildHistory(status));
	}

	private QuestionHistory buildHistory(Status status) {
		return QuestionHistory.builder()
				.status(status)
				.question(this)
				.build();

	}

	public Boolean isEqualsWriter(User loginUser) {
		return this.writer.getId().equals(loginUser.getId());
	}

}
