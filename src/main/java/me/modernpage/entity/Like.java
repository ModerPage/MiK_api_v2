package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="plike")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Like implements Serializable{
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne @JoinColumn(name = "owner_id", nullable = false)
	private Profile owner;


	@JsonBackReference
	@ManyToOne @JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

}
