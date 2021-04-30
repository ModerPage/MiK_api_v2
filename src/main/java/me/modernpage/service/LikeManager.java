package me.modernpage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.modernpage.entity.Like;

public interface LikeManager {
	Like insert(Like like);
	void delete(Like like);
	Like delete(long postId, long ownerId);
	Like findByPostIdAndOwnerId(long postId, long ownerId);
	Page<Like> findLikesByPostId(long postId, Pageable pageable);
	boolean existsByPostIdAndOwnerId(long postId, long ownerId);
}
