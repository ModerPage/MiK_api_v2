package me.modernpage.controller;//package me.modernpage.controller;

import lombok.extern.slf4j.Slf4j;
import me.modernpage.entity.Post;
import me.modernpage.service.CommentManager;
import me.modernpage.service.LikeManager;
import me.modernpage.service.LocationManager;
import me.modernpage.service.PostManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserPostResource {

	@Autowired
	private PostManager postManager;

	@Autowired
	LocationManager locationManager;

	@Autowired
	LikeManager likeManager;

	@Autowired
	CommentManager commentManager;

	@GetMapping("/{userId:[0-9]+}/posts")
	public ResponseUtils getPosts(@PathVariable long userId,
							   @RequestParam(defaultValue = "0",required = false) int page,
							   @RequestParam(defaultValue = "5", required = false) int size) {

		return ResponseUtils.success(postManager.getPostsByOwnerId(userId, PageRequest.of(page, size, Sort.by("created").descending())));
	}

	// TODO: use web socket to inform new post to all other users
	@PostMapping(value = "/{userId:[0-9]+}/posts", consumes = "multipart/form-data", produces = "application/json")
	public ResponseUtils savePost(@PathVariable("userId") long userId, @RequestPart Post post, @RequestParam MultipartFile file) throws IOException{
		return ResponseUtils.success(postManager.savePost(userId, post, file));
	}

}
