package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
	@JoinColumn(name = "writer_id")
//	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@OneToMany(mappedBy = "question")
	@OrderBy("id DESC")
	private List<Answer> answers;

	private String title;

	@Lob
	private String contents;

//	private LocalDateTime createDate;

	@Builder
	public Question(User writer, String title, String content) {
		this.writer = writer;
		this.title = title;
		this.contents = content;
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
