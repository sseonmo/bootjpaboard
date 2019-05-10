package me.seon.bootjpaboard.domain;

import lombok.*;
import me.seon.bootjpaboard.domain.model.Email;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AccountDto {


	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class SignUpReq {

		@NotEmpty
		@Size(max = 20)
		private String userId;

		@NotEmpty
		private String password;

		@NotEmpty
		private String name;

		@Valid // @Valid 반드시 필요
		private Email email;

		@Builder
		public SignUpReq(@NotEmpty @Size(max = 20) String userId, @NotEmpty String password, @NotEmpty String name, Email email) {
			this.userId = userId;
			this.password = password;
			this.name = name;
			this.email = email;
		}

		public User toEntity() {
			return User.builder()
					.userId(this.userId)
					.password(this.password)
					.name(this.name)
					.email(this.email)
					.build();
		}
	}

}
