package me.modernpage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="public_group")
public class PublicGroup extends Group{
	private static final long serialVersionUID = 202103012157L;
}
