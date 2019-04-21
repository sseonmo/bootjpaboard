package me.seon.bootjpaboard.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Result {

	private boolean vaild;

	private String errorMessage;

	public Result() {
		this(true, null);
	}

	public Result(boolean vaild, String errorMessage) {
		this.vaild = vaild;
		this.errorMessage = errorMessage;
	}

	public static Result ok() {
		return new Result(true, null);
	}

	public static Result fail(String message) {
		return new Result(false, message);
	}

}
