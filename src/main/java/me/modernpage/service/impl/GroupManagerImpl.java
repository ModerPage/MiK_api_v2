package me.modernpage.service.impl;

import lombok.Data;
import me.modernpage.entity.*;
import me.modernpage.repository.*;
import me.modernpage.response.MemberPage;
import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.service.GroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Transactional
@ConfigurationProperties(prefix = "upload.path.groupimages")
public class GroupManagerImpl implements GroupManager {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileSystemRepository fileSystemRepository;

	private String default_path;

	public GroupRepository getGroupRepository() {
		return groupRepository;
	}

	public void setGroupRepository(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;
	}

	@Override
	public Optional<Group> findGroupById(long id) {
		// TODO Auto-generated method stub
		return groupRepository.findById(id);
	}

	@Override
	public Page<Post> getAllPostsByGroupId(long groupId, Pageable page) {
		AuthUserDetails principal =(AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String username = principal.getUsername();
		return accountRepository.findAccountByUsername(username).map(account ->
				postRepository.findAllByGroupId(groupId, account.getProfile().getId(), page))
				.<IllegalStateException>orElseThrow(()-> {
					throw new IllegalStateException("Posts loading failed with current user.");
				});
	}

	@Override
	public Optional<List<PrivateGroup>> findGroupsByOwner(Account owner) {
		// TODO Auto-generated method stub
		return groupRepository.findGroupsByOwner(owner);
	}

	@Override
	public Page<PrivateGroup> findAllPrivateGroups(Pageable page) {
		return groupRepository.findAllPrivateGroups(page);
	}

	@Override
	public List<PublicGroup> getAllPublicGroups() {
		// TODO Auto-generated method stub
		return groupRepository.findAllPublicGroups();
	}

	@Override
	public Group saveGroup(long userId, PrivateGroup group) {
		Image image = new Image();
		image.setLocation(default_path);
		image.setName("thumbnail_" + System.currentTimeMillis());
		Image saved_image = imageRepository.save(image);
		group.setImage(saved_image);
		return groupRepository.save(group);
	}

	@Override
	public Group saveGroup(long userId, PrivateGroup group, MultipartFile file) throws IOException {
		String name = "thumbnail_" + System.currentTimeMillis();
		String location = fileSystemRepository.saveGroupImage(file.getBytes(), "private/"+ userId, name);
		Image image = new Image();
		image.setLocation(location);
		image.setName(name);
		Image savedImage = imageRepository.save(image);
		group.setImage(savedImage);
		return groupRepository.save(group);
	}

	@Override
	public Optional<PrivateGroup> findPrivateGroupByIdAndOwnerId(long userId, long ownerId) {
		// TODO Auto-generated method stub
		return groupRepository.findPrivateGroupByIdAndOwnerId(userId, ownerId);
	}

	@Override
	public Long findAllPostsCount(long groupId) {
		// TODO Auto-generated method stub
		return groupRepository.findAllPostsCount(groupId);
	}

	@Override
	public Long findAllMembersCount(long privateGroupId) {
		// TODO Auto-generated method stub
		return groupRepository.findAllMembersCount(privateGroupId);
	}

	@Override
	public List<Group> findGroupsByUserId(long userId) {
		List<PublicGroup> publicGroups = groupRepository.findAllPublicGroups();
		Optional<Collection<PrivateGroup>> privateGroups = userRepository.findById(userId).map(Profile::getJoinedGroups);
		List<Group> all = new ArrayList<>(publicGroups);
		privateGroups.ifPresent(all::addAll);
		return all;
	}

	@Override
	public Slice<Post> findAllPublicGroupPosts(long groupId, Pageable pageable) {
		// TODO Auto-generated method stub
		return groupRepository.findAllPublicGroupPosts(groupId, pageable);
	}

	@Override
	public Optional<FileSystemResource> findGroupImageById(long imageId) {
		Image image = imageRepository.findById(imageId).<IllegalStateException>orElseThrow(() -> {
			throw new IllegalStateException(String.format("Image with %d id not found", imageId));
		});
		return Optional.ofNullable(fileSystemRepository.findInFileSystem(image.getLocation()));
	}

	@Override
	public MemberPage<Profile> findAllMembers(long groupId, long userId, Pageable pageable) {
		Page<Profile> allGroupMembers = userRepository.findAllGroupMembers(groupId, pageable);
		boolean joined = groupRepository.existsByGroupIdAndUserId(groupId, userId);
		return new MemberPage<>(allGroupMembers.getContent(), pageable, allGroupMembers.getTotalElements(), joined);
	}

	@Override
	public Page<Post> findAllPosts(long groupId, Pageable page) {
		AuthUserDetails principal =(AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String username = principal.getUsername();
		return accountRepository.findAccountByUsername(username).map(account ->
				postRepository.findAllByGroupId(groupId, account.getProfile().getId(), page))
				.<IllegalStateException>orElseThrow(()-> {
					throw new IllegalStateException("Posts loading failed with current user.");
				});
	}

	@Override
	public void deleteGroup(long groupId) {
		groupRepository.deleteById(groupId);
	}

	@Override
	public boolean checkJoin(long groupId, long userId) {
		return groupRepository.existsByGroupIdAndUserId(groupId, userId);
	}

	@Override
	public void addMember(long groupId, long userId) {
		groupRepository.addMember(groupId, userId);
	}

	@Override
	public void deleteMember(long groupId, long userId) {
		groupRepository.deleteMember(groupId, userId);
	}
}
