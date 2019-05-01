package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SequenceGenerator(
//		name="ANSWER_SEQ_GEN", //시퀀스 제너레이터 이름
//		sequenceName="ANSWER_SEQ", //시퀀스 이름
//		initialValue=1, //시작값
//		allocationSize=1 //메모리를 통해 할당할 범위 사이즈
//)
public class Answer extends AbstractEntity {

//	@Id
//	@GeneratedValue()
////	@GeneratedValue(generator = "ANSWER_SEQ_GEN")
//	private Long id;

	@ManyToOne
	@JoinColumn(name = "writer_id")
	private User writer;

	@ManyToOne
	@JoinColumn(name = "question_id")
//	@JsonBackReference
	private Question question;

	@Lob
	private String contents;

/*
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	public String getAnswerCreateDate() {
		return this.createDate == null ? "" : this.createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}*/

	@Builder
	public Answer(User writer, Question question, String contents) {
		this.writer = writer;
		this.question = question;
		this.contents = contents;
	}

	public Boolean isEqualsWriter(User loginUser) {
		return this.getWriter().getId().equals(loginUser.getId());
	}
}
