package me.modernpage.controller;

import me.modernpage.entity.Comment;
import me.modernpage.entity.Like;
import me.modernpage.entity.Post;
import me.modernpage.response.LikePage;
import me.modernpage.service.PostManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("posts")
@RequestMapping("/posts")
public class PostResource {

    @Autowired
    private PostManager postManager;

    @GetMapping
    public ResponseUtils getPosts(@RequestParam(defaultValue = "0",required = false) int page,
                                  @RequestParam(defaultValue = "5", required = false) int size) {
        return ResponseUtils.success(postManager.findAll(PageRequest.of(page, size)));
    }

    @GetMapping(path = "/{postId:[0-9]+}/files/{fileId:[0-9]+}/**", produces = {
            MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public FileSystemResource getFile(@PathVariable long postId, @PathVariable long fileId) {
        return postManager.findPostFileById(fileId).<IllegalStateException>orElseThrow(()->{
            throw new IllegalStateException(String.format("File with %d id not found", fileId));
        });
    }

    @PutMapping(value = "/{postId:[0-9]+}", consumes = "application/json", produces = "application/json")
	public ResponseUtils updatePost(@PathVariable long postId, @RequestBody Post post) {
		return ResponseUtils.success(postManager.savePost(post));
	}

    // TODO: use web socket to inform new post to all other users
    @PostMapping(value = "/{postId:[0-9]+}", consumes = "multipart/form-data", produces = "application/json")
    public ResponseUtils updatePost(@RequestPart Post post, @RequestParam MultipartFile file) throws IOException {
        return ResponseUtils.success(postManager.savePost(post.getOwner().getId(), post, file));
    }

    @DeleteMapping(value="/{postId:[0-9]+}")
	public ResponseUtils deletePost(@PathVariable long postId) {
        postManager.removeById(postId);
	    return ResponseUtils.success(null);
    }

    @GetMapping(value = "/{postId:[0-9]+}")
    public ResponseUtils hidePost(@PathVariable long postId, @RequestParam long userId) {
        postManager.hidePost(userId, postId);
        return ResponseUtils.success(null);
    }

    @GetMapping(value="/{postId:[0-9]+}/likes", produces = "application/json")
	public ResponseUtils getLikes(@PathVariable long postId,
			@RequestParam(defaultValue = "0",required = false) int page,
			@RequestParam(defaultValue = "5", required = false) int size,
			@RequestParam long ownerId) {
        LikePage<Like> created = postManager.findLikesByPostId(postId, ownerId,
                PageRequest.of(page, size, Sort.by("created").descending()));
        return ResponseUtils.success(created);
	}

    @DeleteMapping(value="/{postId:[0-9]+}/likes", produces="application/json")
	public ResponseUtils deletePostLike(@PathVariable long postId, @RequestParam long ownerId) {
		return ResponseUtils.success(postManager.deleteLike(postId, ownerId));
	}
    // TODO: use web socket to inform about aP new like to the post owner
	@PostMapping(value="/{postId:[0-9]+}/likes", consumes = "application/json", produces="application/json")
	public ResponseUtils insertPostLike(@PathVariable long postId, @RequestBody Like like) {
		return ResponseUtils.success(postManager.saveLike(like));
	}

    @GetMapping(value="/{postId:[0-9]+}/comments", produces = "application/json")
	public ResponseUtils getComments(@PathVariable long postId,
			@RequestParam(defaultValue = "0",required = false) int page,
			@RequestParam(defaultValue = "5", required = false) int size) {
        Page<Comment> commentPage = postManager.findCommentsByPostId(postId, PageRequest.of(page, size, Sort.by("created").descending()));
        return ResponseUtils.success(commentPage);
	}

	@DeleteMapping(value = "/{postId:[0-9]+}/comments")
    public ResponseUtils deleteComments(@PathVariable long postId, @RequestParam long commentId) {
        postManager.deleteComment(commentId);
        return ResponseUtils.success(null);
    }

    // TODO: use web socket to inform about a new comment to the post owner
	@PostMapping(value="/{postId:[0-9]+}/comments", consumes="application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseUtils insertPostComment(@PathVariable long postId, @RequestBody Comment comment) {
        return ResponseUtils.success(postManager.saveComment(comment));
	}

}
