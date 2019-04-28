package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
//@SequenceGenerator(
//		name="QUESTION_SEQ_GEN", //시퀀스 제너레이터 이름
//		sequenceName="QUESTION_SEQ", //시퀀스 이름
//		initialValue=1, //시작값
//		allocationSize=1 //메모리를 통해 할당할 범위 사이즈
//)
public class Question extends AbstractEntity{

//	@Id @GeneratedValue
////	@Id @GeneratedValue(generator = "QUESTION_SEQ_GEN")
//	private Long id;

	@ManyToOne
	@JoinColumn(name = "writer_id")
//	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@OneToMany(mappedBy = "question")
	@OrderBy("id DESC")
//	@JsonManagedReference
	private List<Answer> answers;

	private String title;

	@Lob
	private String contents;

//	private LocalDateTime createDate;

	public Question(User writer, String title, String content) {
		this.writer = writer;
		this.title = title;
		this.contents = content;
//		this.createDate = LocalDateTime.now();
	}

//	public String getFormattedCreateDate() {
//		return  contents != null ? createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
//	}

	public void update(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}

	public Boolean isEqualsWriter(User loginUser) {
		return this.writer.getId().equals(loginUser.getId());
	}

}
