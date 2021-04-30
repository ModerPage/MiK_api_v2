package me.modernpage.service.impl;

import me.modernpage.service.CommentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.modernpage.entity.Comment;
import me.modernpage.repository.CommentRepository;

@Service
@Transactional
public class CommentManagerImpl implements CommentManager {

	@Autowired
	private	CommentRepository commentRepository;
	
	@Override
	public Page<Comment> findAllByPostId(long postId, Pageable pageable) {
		// TODO Auto-generated method stub
		return commentRepository.findAllByPostId(postId, pageable);
	}

	@Override
	public Comment insert(Comment comment) {
		// TODO Auto-generated method stub
		return commentRepository.save(comment);
	}

}
