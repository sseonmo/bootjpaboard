package me.seon.bootjpaboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class Question {

	@Id @GeneratedValue
	private Long id;

	private  String writer;

	private String title;

	private String contents;

	public Question(String writer, String title, String content) {
		this.writer = writer;
		this.title = title;
		this.contents = content;
	}
}
