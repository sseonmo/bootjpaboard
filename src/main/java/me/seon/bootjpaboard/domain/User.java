package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SequenceGenerator(
//		name="USER_SEQ_GEN", //시퀀스 제너레이터 이름
//		sequenceName="USER_SEQ", //시퀀스 이름
//		initialValue=1, //시작값
//		allocationSize=1 //메모리를 통해 할당할 범위 사이즈
//)
public class User extends AbstractEntity{

	/*@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "USER_SEQ_GEN")
	private Long id;
*/
	@Column(nullable = false, length = 20, unique = true)
	private String userId;

	@JsonIgnore
	private String password;

	private String name;

	private String email;

//	@OneToMany(mappedBy = "writer")
//	private List<Question> questionList = new ArrayList<>();

	@Builder
	public User(String userId, String password, String name, String email) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public boolean matchId(Long newId) {
		if( newId == null ) return false;
		return newId.equals(super.getId());
	}

	public boolean matchPassword(String newPassword) {
		if( newPassword == null ) return false;
		return newPassword.equals(password);
	}
}
