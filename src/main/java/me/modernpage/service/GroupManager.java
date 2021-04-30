package me.modernpage.service;

import me.modernpage.entity.*;
import me.modernpage.response.MemberPage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GroupManager {
	Page<PrivateGroup> findAllPrivateGroups(Pageable page);
	List<PublicGroup> getAllPublicGroups();
	Group saveGroup(long userId, PrivateGroup group);
	Group saveGroup(long userId, PrivateGroup group, MultipartFile file) throws IOException;
	List<Group> findGroupsByUserId(long userId);
	Optional<Group> findGroupById(long id);
	Page<Post> getAllPostsByGroupId(long groupId, Pageable page);
	Optional<List<PrivateGroup>> findGroupsByOwner(Account owner);
	Optional<PrivateGroup> findPrivateGroupByIdAndOwnerId(long userId, long ownerId);
	Long findAllPostsCount(long groupId);
	Long findAllMembersCount(long privateGroupId);
	Slice<Post> findAllPublicGroupPosts(long groupId, Pageable pageable);

	Optional<FileSystemResource> findGroupImageById(long imageId);

	MemberPage<Profile> findAllMembers(long groupId, long userId, Pageable pageable);

	Page<Post> findAllPosts(long groupId, Pageable pageable);

	void deleteGroup(long groupId);

	boolean checkJoin(long groupId, long userId);

	void addMember(long groupId, long userId);

	void deleteMember(long groupId, long userId);
}
