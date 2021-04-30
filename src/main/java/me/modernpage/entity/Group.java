package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="pgroup")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"posts"})
public class Group extends RepresentationModel<Group> implements Serializable {
	
	private static final long serialVersionUID = 202102081251L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable=false)
	private String name;
	
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "image_id", nullable=false)
	private Image image;

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Post> posts = new ArrayList<>();

}
