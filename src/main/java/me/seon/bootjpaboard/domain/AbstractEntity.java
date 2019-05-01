package me.seon.bootjpaboard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter @Getter
class AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	private Long id;

	@CreatedDate
//	@CreationTimestamp
	@Column(updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	@LastModifiedDate
//	@UpdateTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifyDate;


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



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractEntity that = (AbstractEntity) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(createDate, that.createDate) &&
				Objects.equals(modifyDate, that.modifyDate);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, createDate, modifyDate);
	}

	@Override
	public String toString() {
		return "AbstractEntity{" +
				"id=" + id +
				", createDate=" + createDate +
				", modifyDate=" + modifyDate +
				'}';
	}
}


