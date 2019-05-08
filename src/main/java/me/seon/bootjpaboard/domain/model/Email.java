package me.seon.bootjpaboard.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@JsonIgnoreProperties({"host", "id"})
public class Email {

//	@javax.validation.constraints.Email
	@Column(name = "email", nullable = false, unique = true)
	private String value;

	@Builder
	public Email(String value) {
		this.value = value;
	}

	public static Email of(String email) {
		return new Email(email);
	}

	public String getId() {
		int index = this.value.indexOf("@");
		return this.value.substring(0, index);
	}

	public String getHost() {
		int index = this.value.indexOf("@");
		return this.value.substring(index);
	}


}
