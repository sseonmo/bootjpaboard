package me.seon.bootjpaboard.exception;

import lombok.Getter;
import me.seon.bootjpaboard.domain.model.Email;

@Getter
public class EmailDuplicationException extends BasicException {

	private String field;
	private Email email;

	public EmailDuplicationException(Email email) {
		this.field = "email";
		this.email = email;
	}
}
