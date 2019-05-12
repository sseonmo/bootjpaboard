package me.seon.bootjpaboard.domain.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

	@Column(name = "password", nullable = false)
	private String value;

	@Column(name = "password_expiratrion_date")
	private LocalDateTime expirationDate;

	@Column(name = "password_failed_count", nullable = false)
	private int failedCount;

	@Column(name = "pasword_ttl")
	private long ttl;

	@Builder
	public Password(final String value) {
//		this.ttl = 2109_604;
		this.ttl = 60 * 60 * 24 * 14; // 14일
		this.value = value;
		this.expirationDate = extendExpirationDate();
	}

	private LocalDateTime extendExpirationDate() {
		return LocalDateTime.now().plusSeconds(ttl);
	}


	public Boolean isMatched(final String password) {

		// 비밀번호 실패 제한이 있을경우
//		if(failedCount > 5) throw new PasswordFaildExceed();

		Boolean result = isMatches(password);

		updateFailedCount(result);

		return result;
	}

	private Boolean isMatches(String password) {
		return this.value.equals(password) ;
	}

	private void updateFailedCount(boolean matches) {
		if (matches)
			resetFailedCount();
		else
			increaseFailedCount();
	}

	private void resetFailedCount() {
		this.failedCount = 0;
	}

	private void increaseFailedCount() {
		this.failedCount++;
	}

	// 비밀번호 유효기간이 정해져있을때...
	public Boolean isExpiration() {
		return LocalDateTime.now().isAfter(this.expirationDate);
	}

	// 비밀번호 변경
	public Boolean changePassword(final String oldPassword) {
		Boolean result = isMatches(oldPassword);
		if(result)  this.expirationDate = extendExpirationDate();

		return result;

	}

}


