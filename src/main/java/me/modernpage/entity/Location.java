package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="plocation",
		uniqueConstraints =
				{@UniqueConstraint(columnNames = {"LONGITUDE", "LATITUDE"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"posts"})
public class Location implements Serializable {
	
	private static final long serialVersionUID = 202102081323L;
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long id;

	@Column(nullable = false)
	private double longitude;
	
	@Column(nullable = false)
	private double latitude;
	
	@Column(nullable = false)
	private String addressLine;
	
	private String city;
	
	private String country;


	@OneToMany(mappedBy = "location")
	@NotFound(action = NotFoundAction.IGNORE)
	private Collection<Post> posts = new ArrayList<>();
}
