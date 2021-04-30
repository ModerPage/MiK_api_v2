package me.modernpage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="private_group")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"members", "posts"}, allowSetters = true)
public class PrivateGroup extends Group {

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private Profile owner;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="group_members",
				joinColumns = {@JoinColumn(name="group_id")},
				inverseJoinColumns = {@JoinColumn(name="profile_id")})
	private Collection<Profile> members = new ArrayList<Profile>();
}
