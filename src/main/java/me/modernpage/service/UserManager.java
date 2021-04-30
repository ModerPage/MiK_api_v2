package me.modernpage.service;

import me.modernpage.entity.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface UserManager {
	Profile saveUser(Profile user);
	List<Profile> findAll(Sort sort);
	void deleteUserById(long id);
	Optional<Profile> findUserById(long id);
	Optional<FileSystemResource> findUserImageById(long id);

    Page<Profile> getFollowers(long userId, Pageable page, Long followerId);
    Page<Profile> getFollowing(long userId, Pageable page);

	void addRequest(long userId, long requesterId);

	void deleteRequest(long userId, long requesterId);

	void deleteFollower(long userId, long followerId);

	void addFollower(long userId, long followerId);

	void addFollowing(long followerId, long userId);

	void deleteFollowing(long followerId, long userId);
}
