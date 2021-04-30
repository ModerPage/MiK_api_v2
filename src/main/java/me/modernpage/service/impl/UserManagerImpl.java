package me.modernpage.service.impl;

import me.modernpage.entity.Image;
import me.modernpage.entity.Profile;
import me.modernpage.repository.FileSystemRepository;
import me.modernpage.repository.ImageRepository;
import me.modernpage.repository.UserRepository;
import me.modernpage.response.FollowPage;
import me.modernpage.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserManagerImpl implements UserManager {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private FileSystemRepository fileSystemRepository;

	@Override
	public Profile saveUser(Profile user) {
		// TODO Auto-generated method stub
		return userRepository.save(user);
	}

	@Override
	public List<Profile> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return userRepository.findAll(sort);
	}

	@Override
	public void deleteUserById(long id) {
		// TODO Auto-generated method stub
		userRepository.deleteById(id);
	}

	@Override
	public Optional<Profile> findUserById(long id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id);
	}

	@Override
	public Optional<FileSystemResource> findUserImageById(long id) {
		Image image = imageRepository.findById(id).<IllegalStateException>orElseThrow(()->{
			throw new IllegalStateException(String.format("Image with %d id not found", id));
		});
		return Optional.ofNullable(fileSystemRepository.findInFileSystem(image.getLocation()));
	}


	@Override
	public Page<Profile> getFollowers(long userId, Pageable page, Long followerId) {
		Page<Profile> allFollowers = userRepository.findAllFollowers(userId, page);
		boolean isFollowed = false;
		boolean isRequested = false;
		if(allFollowers.getTotalElements() > 0 && followerId != null) {
			isFollowed = userRepository.existsFollower(userId, followerId);
		} else {
			isRequested = userRepository.existsRequest(userId, followerId);
		}
		return new FollowPage(allFollowers.getContent(), page, allFollowers.getTotalElements(), isFollowed, isRequested);
	}

	@Override
	public Page<Profile> getFollowing(long userId, Pageable page) {
		return userRepository.findAllFollowing(userId, page);
	}

	@Override
	public void addRequest(long userId, long requesterId) {
		userRepository.addRequest(userId, requesterId);
	}

	@Override
	public void deleteRequest(long userId, long requesterId) {
		userRepository.deleteRequest(userId, requesterId);
	}

	@Override
	public void deleteFollower(long userId, long followerId) {
		userRepository.deleteFollower(userId, followerId);
	}

	@Override
	public void addFollower(long userId, long followerId) {
		userRepository.addFollower(userId, followerId);
	}

	@Override
	public void addFollowing(long followerId, long userId) {
		userRepository.addFollowing(followerId, userId);
	}

	@Override
	public void deleteFollowing(long followerId, long userId) {
		userRepository.deleteFollowing(followerId, userId);
	}
}
