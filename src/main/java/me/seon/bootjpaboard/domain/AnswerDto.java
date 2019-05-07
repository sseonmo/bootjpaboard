package me.seon.bootjpaboard.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

public class AnswerDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class CreateReq {

		@NotEmpty
		private String contents;

	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Res {

		private Long answerId;

		private Long questionId;

		private String userId;

		private String contents;

		private String createDate;


		public Res(Answer answer) {
			this.answerId = answer.getId();
			this.questionId = answer.getQuestion().getId();
			this.userId = answer.getWriter().getUserId();
			this.contents = answer.getContents();
			this.createDate = answer.getFormattedCreateDate();
		}

	}


}
