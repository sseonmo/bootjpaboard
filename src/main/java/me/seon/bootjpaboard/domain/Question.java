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
public class Question {

	@Id @GeneratedValue
	private Long id;

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

	private LocalDateTime createDate;

	public Question(User writer, String title, String content) {
		this.writer = writer;
		this.title = title;
		this.contents = content;
		this.createDate = LocalDateTime.now();
	}

	public String getFormattedCreateDate() {
		return  contents != null ? createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
	}

	public void update(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}

	public Boolean isEqualsWriter(User loginUser) {
		return this.writer.getId().equals(loginUser.getId());
	}

	@Override
	public String toString() {
		return "Question{" +
				"id=" + id +
				", writer=" + writer +
				", title='" + title + '\'' +
				", contents='" + contents + '\'' +
				", createDate=" + createDate +
				'}';
	}
}
