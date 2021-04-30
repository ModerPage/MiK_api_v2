package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name="account", uniqueConstraints = {
		@UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "username")})
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"roles"})
public class Account extends RepresentationModel<Account> implements Serializable {
	private static final long serialVersionUID = 202101081210L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String username;

	private String email;
	private String password;
	private boolean active;
	
	@OneToOne
	@JoinColumn(name = "profile_id", nullable = false)
	private Profile profile; 
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_role", joinColumns = {@JoinColumn(name="user_id")},
			inverseJoinColumns = {@JoinColumn(name="role_id")})
	private Set<Role> roles = new HashSet<>();
}
