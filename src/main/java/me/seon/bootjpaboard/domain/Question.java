package me.seon.bootjpaboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

	private String title;

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
}
