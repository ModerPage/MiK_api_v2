package me.modernpage.service.impl;



import me.modernpage.service.LikeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.modernpage.entity.Like;
import me.modernpage.repository.LikeRepository;

@Service
@Transactional
public class LikeManagerImpl implements LikeManager {
	@Autowired
	private LikeRepository likeRepository;
	
	@Override
	public Like insert(Like like) {
		// TODO Auto-generated method stub
		return likeRepository.save(like);
	}

	@Override
	public void delete(Like like) {
		// TODO Auto-generated method stub
		likeRepository.delete(like);
	}

	@Override
	public Like findByPostIdAndOwnerId(long postId, long ownerId) {
		// TODO Auto-generated method stub
		return likeRepository.findByPostIdAndOwnerId(postId, ownerId);
	}

	@Override
	public Page<Like> findLikesByPostId(long postId, Pageable pageable) {
		// TODO Auto-generated method stub
		return likeRepository.findLikesByPostId(postId, pageable);
	}

	@Override
	public boolean existsByPostIdAndOwnerId(long postId, long ownerId) {
		// TODO Auto-generated method stub
		return likeRepository.existsByPostIdAndOwnerId(postId, ownerId);
	}

	@Override
	public Like delete(long postId, long ownerId) {
		// TODO Auto-generated method stub
		return likeRepository.deleteByPostIdAndOwnerId(postId, ownerId);
	}

}
