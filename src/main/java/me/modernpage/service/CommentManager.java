package me.modernpage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import me.modernpage.entity.Comment;
import me.modernpage.entity.Like;

public interface CommentManager {

	Page<Comment> findAllByPostId(long postId, Pageable pageable);
	Comment insert(Comment comment);

}
