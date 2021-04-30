package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "profile")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"createdGroups", "posts", "joinedGroups", "hiddenPosts", "followers", "following", "requests"})
public class Profile extends RepresentationModel<Profile> implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String fullname;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date birthdate;
	
	@OneToOne
	@JoinColumn(name = "image_id", nullable = false)
	private Image image;

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable=false) @Temporal(TemporalType.DATE)
	private Date created;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "owner")
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<PrivateGroup> createdGroups = new ArrayList<PrivateGroup>();

	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "members")
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<PrivateGroup> joinedGroups = new ArrayList<PrivateGroup>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "hidden_posts", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "post_id")})
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Post> hiddenPosts = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_followers", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "follower_id")})
	private Collection<Profile> followers = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_following", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "following_id")})
	private Collection<Profile> following = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "follower_request", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "requester_id")})
	private Collection <Profile> requests = new ArrayList<>();
}
