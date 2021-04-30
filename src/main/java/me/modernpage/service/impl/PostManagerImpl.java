package me.modernpage.service.impl;

import me.modernpage.entity.*;
import me.modernpage.repository.*;
import me.modernpage.response.LikePage;
import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.service.PostManager;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PostManagerImpl implements PostManager {
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private FileSystemRepository fileSystemRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private CommentRepository commentRepository;

	private final Tika tika = new Tika();

	// only for saving new post, not for updating
	@Transactional
	@Override
	public Post savePost(long userId, Post post, MultipartFile file) throws IOException {
		String name = String.valueOf(System.currentTimeMillis());
		String fileLocation = fileSystemRepository
				.savePostFile(file.getBytes(), String.valueOf(userId), name);

		Location location = post.getLocation();
		if(location != null) {
			Location existingLocation = locationRepository.
					findLocationByLongitudeAndLatitude(location.getLongitude(), location.getLatitude());
			if(existingLocation == null)
				location = locationRepository.save(location);
			else
				location = existingLocation;
			post.setLocation(location);
		}

		File newFile = new File();
		newFile.setLocation(fileLocation);
		newFile.setName(name);
		newFile.setType(tika.detect(fileLocation).split("/")[0]);
		newFile = fileRepository.save(newFile);

		post.setFile(newFile);
		userRepository.findById(userId).ifPresent(post::setOwner);
		return postRepository.save(post);
	}

	@Override
	public Post savePost(Post post) {
		return postRepository.save(post);
	}

	@Transactional
	@Override
	public void removeById(long postId) {
		postRepository.deleteById(postId);
	}

	@Transactional
	@Override
	public Optional<Location> getLocationById(long postId) {
		return postRepository.findLocationById(postId);
	}

	@Transactional
	@Override
	public Optional<Account> getOwnerById(long postId) {
		return postRepository.findOwnerById(postId);
	}

	@Transactional
	@Override
	public Optional<Group> getGroupById(long postId) {
		return postRepository.findGroupById(postId);
	}

	@Transactional
	@Override
	public Optional<Post> getPostById(long postId) {
		return postRepository.findById(postId);
	}

	@Transactional
	@Override
	public Page<Post> getPostsByOwnerId(long ownerId, Pageable page) {
		return postRepository.findAllByOwnerId(ownerId, page);
	}

	@Transactional
	@Override
	public Long getAllPostLikesCount(long postId) {
		return postRepository.findAllPostLikesCount(postId);
	}

	@Transactional
	@Override
	public Long getAllPostCommentsCount(long postId) {
		return postRepository.findAllPostCommentsCount(postId);
	}

	@Transactional
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

	@Transactional
	@Override
	public Page<Post> findAll(Pageable page) {
		AuthUserDetails principal =(AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String username = principal.getUsername();
		return accountRepository.findAccountByUsername(username).map(account -> {
			Profile profile = account.getProfile();
			return postRepository.findAll(profile.getId(), page);
		}).<IllegalStateException>orElseThrow(()->{
			throw new IllegalStateException("Posts loading failed with current user.");
		});
	}

	@Transactional
	@Override
	public Optional<FileSystemResource> findPostFileById(long fileId) {
		File file = fileRepository.findById(fileId).<IllegalStateException>orElseThrow(() -> {
			throw new IllegalStateException(String.format("File with %d id not found", fileId));
		});
		return Optional.ofNullable(fileSystemRepository.findInFileSystem(file.getLocation()));
	}

	@Transactional
	@Override
	public LikePage<Like> findLikesByPostId(long postId, long ownerId, Pageable pageable) {
		Page<Like> likePage = likeRepository.findLikesByPostId(postId, pageable);
		boolean isLiked = false;
		if(ownerId > 0) {
			isLiked = likeRepository.existsByPostIdAndOwnerId(postId, ownerId);
		}
		return new LikePage<>(likePage.getContent(), pageable, likePage.getTotalElements(), isLiked);
	}

	@Transactional
	@Override
	public Like deleteLike(long postId, long ownerId) {
		Like like = likeRepository.findByPostIdAndOwnerId(postId, ownerId);
		likeRepository.delete(like);
		return like;
	}

	@Override
	@Transactional
	public Like saveLike(Like like) {
		return likeRepository.save(like);
	}

	@Transactional
	@Override
	public void hidePost(long userId, long postId) {
		postRepository.hidePost(userId, postId);
	}

	@Override
	public Page<Comment> findCommentsByPostId(long postId, Pageable pageable) {
		return commentRepository.findAllByPostId(postId, pageable);
	}

	@Override
	public Comment saveComment(Comment comment) {
		return commentRepository.save(comment);
	}

	@Override
	public void deleteComment(long commentId) {
		commentRepository.deleteById(commentId);
	}

	@Transactional
	@Override
	public void deletePostById(long postId) {
		// TODO Auto-generated method stub
		postRepository.deleteById(postId);
	}
}
