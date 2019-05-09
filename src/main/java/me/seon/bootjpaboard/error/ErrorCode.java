package me.seon.bootjpaboard.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

	INPUT_VALUE_INVALID("CM_001", "입력값이 올바르지 않습니다.", 400)
	,ACCOUNT_NOT_FOUNT("AC_001", "해당 회원을 찾을 수 없습니다.", 404)
	,EMAIL_DUPLICATE("AC_002", "이메일이 중복되었습니다.", 400)
	,USERID_DUPLICATE("AC_003", "사용자ID가 중복되었습니다.", 400)
	;


	private final String code;
	private final String message;
	private final int status;

	ErrorCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
