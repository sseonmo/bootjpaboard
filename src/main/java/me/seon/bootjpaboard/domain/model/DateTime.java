package me.seon.bootjpaboard.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateTime {

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createDate;

	@LastModifiedDate
	@Column(name = "update_at")
	private LocalDateTime updateDate;

}

