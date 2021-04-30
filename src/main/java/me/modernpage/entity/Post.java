package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "post")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"likes", "comments", "hided_by"}, allowGetters = true)
public class Post extends RepresentationModel<Post> implements Serializable {
	
	private static final long serialVersionUID = 202102081234L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private long id;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private Profile owner;

	@ManyToOne
	@JoinColumn(name = "location_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Location location;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true) 
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Comment> comments = new ArrayList<>();

	@ManyToOne 
	@JoinColumn(name = "group_id", nullable=false)
	private Group group;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Like> likes = new ArrayList<>();

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@ManyToMany(mappedBy = "hiddenPosts")
	@Column(name = "hided_by")
	private Collection<Profile> hidedBy = new ArrayList<>();
	
	@Column(nullable=false)
	private String text;
	
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "file_id", nullable = false)
	private File file;
}
