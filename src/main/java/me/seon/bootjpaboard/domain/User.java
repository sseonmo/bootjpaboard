package me.seon.bootjpaboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Setter @Getter @ToString
public class User {

	@Id @GeneratedValue
	private Long id;

	@Column(nullable = false, length = 20)
	private String userId;

	private String password;

	private String name;

	private String email;

}
