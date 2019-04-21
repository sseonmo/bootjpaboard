package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.seon.bootjpaboard.config.LocalDateTimeConverter;
import org.apache.tomcat.jni.Local;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@ToString
@Setter @Getter
@NoArgsConstructor
public class Answer {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "writer_id")
	private User writer;

	@ManyToOne
	@JoinColumn(name = "question_id")
//	@JsonBackReference
	@JsonIgnore
	private Question question;

	@Lob
	private String contents;


	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	public String getAnswerCreateDate() {
		return this.createDate == null ? "" : this.createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public Answer(User writer, Question question, String contents) {
		this.writer = writer;
		this.question = question;
		this.contents = contents;
		this.createDate = LocalDateTime.now();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Answer answer = (Answer) o;
		return Objects.equals(id, answer.id) &&
				Objects.equals(writer, answer.writer) &&
				Objects.equals(contents, answer.contents) &&
				Objects.equals(createDate, answer.createDate);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, writer, contents, createDate);
	}
}
