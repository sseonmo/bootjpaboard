package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User extends AbstractEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20, unique = true)
	private String userId;


	@JsonIgnore
	@NotEmpty
	@Column(nullable = false)
	private String password;

	@NotEmpty
	@Column(nullable = false)
	private String name;

	@Email
	@Column(nullable = false, unique = true)
	private String email;

//	@OneToMany(mappedBy = "writer")
//	private List<Question> questionList = new ArrayList<>();

	public void updateAccout(User user) {
		this.userId = user.getUserId();
		this.password = user.getPassword();
		this.name = user.getName();
		this.email = user.getEmail();

	}

	@Builder
	public User( String userId, String password, String name, String email) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public boolean matchId(Long newId) {
		if( newId == null ) return false;
		return newId.equals(this.getId());
	}

	public boolean matchPassword(String newPassword) {
		if( newPassword == null ) return false;
		return newPassword.equals(password);
	}
}
