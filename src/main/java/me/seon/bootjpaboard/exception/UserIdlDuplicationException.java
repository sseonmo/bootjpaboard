package me.seon.bootjpaboard.exception;

import lombok.Getter;

@Getter
public class UserIdlDuplicationException extends BasicException {

	private String userId;

	public UserIdlDuplicationException(String userId) {
		this.userId = userId;
	}
}
