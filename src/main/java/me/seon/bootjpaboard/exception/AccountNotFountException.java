package me.seon.bootjpaboard.exception;


import lombok.Getter;

@Getter
public class AccountNotFountException extends BasicException {

	private String id;

	public AccountNotFountException(String id) {
		this.id = id;
	}
}
