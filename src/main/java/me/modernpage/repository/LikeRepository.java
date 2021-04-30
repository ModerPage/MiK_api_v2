package me.modernpage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.modernpage.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{
	Like deleteByPostIdAndOwnerId(long postId, long ownerId);
	Like findByPostIdAndOwnerId(long postId, long ownerId);
	Page<Like> findLikesByPostId(long postId, Pageable pageable);
	boolean existsByPostIdAndOwnerId(long postId, long ownerId);
}
