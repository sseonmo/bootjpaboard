package me.seon.bootjpaboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter @ToString
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20, unique = true)
	private String userId;

	private String password;

	private String name;

	private String email;

//	@OneToMany(mappedBy = "writer")
//	private List<Question> questionList = new ArrayList<>();

	public boolean matchId(Long newId) {
		if( newId == null ) return false;
		return newId.equals(id);
	}

	public boolean matchPassword(String newPassword) {
		if( newPassword == null ) return false;
		return newPassword.equals(password);
	}
}
