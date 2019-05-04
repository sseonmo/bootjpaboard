package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
class AbstractEntity {


	@CreatedDate
//	@CreationTimestamp
	@Column(updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;


	@LastModifiedDate
//	@UpdateTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifyDate;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;

	public String getFormattedCreateDate() {
		return dateFormatter(this.createDate, "yyyy-MM-dd HH:mm:ss");
	}

	public String getFormattedModifyDate() {
		return dateFormatter(this.modifyDate, "yyyy-MM-dd HH:mm:ss");
	}

	private String dateFormatter(LocalDateTime date, String patten) {
		if(date == null || patten == null)    return "";
		try {
			return date.format(DateTimeFormatter.ofPattern(patten));
		} catch (Exception e) {
			return "";
		}
	}


}


