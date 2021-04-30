package me.modernpage.service;

import me.modernpage.entity.*;
import me.modernpage.response.LikePage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface PostManager {
	Post savePost(long userId, Post post, MultipartFile file) throws IOException;
	Post savePost(Post post);
	void removeById(long postId);
	Optional<Location> getLocationById(long postId);
	Optional<Account> getOwnerById(long postId);
	Optional<Group> getGroupById(long postId);
	Optional<Post> getPostById(long postId);
	void deletePostById(long postId);
	Page<Post> getPostsByOwnerId(long ownerId, Pageable page);
	Long getAllPostLikesCount(long postId);
	Long getAllPostCommentsCount(long postId);
	Page<Post> getAllPostsByGroupId(long groupId, Pageable page);
	Page<Post> findAll(Pageable page);
    Optional<FileSystemResource> findPostFileById(long fileId);
    LikePage<Like> findLikesByPostId(long postId, long ownerId, Pageable pageable);

	Like deleteLike(long postId, long ownerId);
	Like saveLike(Like like);

	void hidePost(long userId, long postId);

	Page<Comment> findCommentsByPostId(long postId, Pageable pageable);

	Comment saveComment(Comment comment);

	void deleteComment(long commentId);
}
