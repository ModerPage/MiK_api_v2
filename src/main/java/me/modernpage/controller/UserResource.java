package me.modernpage.controller;//package me.modernpage.controller;

import me.modernpage.entity.Profile;
import me.modernpage.service.UserManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserManager userManager;

	@GetMapping(path = "/{userId:[0-9]+}", consumes = "application/json", produces = "application/json")
	public ResponseUtils getUser(@PathVariable Long userId) {
		Optional<Profile> profileOptional = userManager.findUserById(userId);
		return profileOptional.map(ResponseUtils::success)
				.orElseGet(() -> ResponseUtils.response(500, String.format("User with %d id not found", userId), null));
	}

	@GetMapping(path = "/{userId:[0-9]+}/followers")
	public ResponseUtils getFollowers(@PathVariable long userId,
									  @RequestParam(defaultValue = "0",required = false) int page,
									  @RequestParam(defaultValue = "5", required = false) int size,
									  @RequestParam(required = false) Long followerId) {
		return ResponseUtils.success(userManager.getFollowers(userId, PageRequest.of(page, size), followerId));
	}

//	@DeleteMapping(path = "/{userId:[0-9]+}/followers")
//	public ResponseUtils deleteFollower(@PathVariable long userId,
//										@RequestParam(required = false) long followerId) {
//		userManager.deleteFollower(userId, followerId);
//		return ResponseUtils.success(null);
//	}

//	//TODO: use web socket to inform new request to the receiver
//	@PostMapping(path = "/{userId:[0-9]+}/followers")
//	public ResponseUtils addRequest(@PathVariable long userId, @RequestParam long requesterId) {
//		userManager.addRequest(userId, requesterId);
//		return ResponseUtils.success(null);
//	}
//
//	@DeleteMapping(path = "/{userId:[0-9]+}/followers")
//	public ResponseUtils deleteRequest(@PathVariable long userId, @RequestParam long requesterId) {
//		userManager.deleteRequest(userId, requesterId);
//		return ResponseUtils.success(null);
//	}

	@PostMapping(path = "/{userId:[0-9]+}/followers")
	public ResponseUtils addFollower(@PathVariable long userId, @RequestParam long followerId) {
		userManager.addFollower(userId, followerId);
		userManager.addFollowing(followerId, userId);
		return ResponseUtils.success(null);
	}

	@DeleteMapping(path = "/{userId:[0-9]+}/followers")
	public ResponseUtils deleteFollower(@PathVariable long userId, @RequestParam long followerId) {
		userManager.deleteFollower(userId, followerId);
		userManager.deleteFollowing(followerId, userId);
		return ResponseUtils.success(null);
	}

	@GetMapping(path = "/{userId:[0-9]+}/following")
	public ResponseUtils getFollowing(@PathVariable long userId,
									  @RequestParam(defaultValue = "0",required = false) int page,
									  @RequestParam(defaultValue = "5", required = false) int size) {
		return ResponseUtils.success(userManager.getFollowing(userId, PageRequest.of(page, size)));
	}

	@GetMapping(path = "/{userId:[0-9]+}/images/{imageId:[0-9]+}/**",
			produces = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	public FileSystemResource getImage(@PathVariable Long userId, @PathVariable Long imageId) {
		return userManager.findUserImageById(imageId).<IllegalStateException>orElseThrow(()-> {
			throw new IllegalStateException(String.format("Image with %d id not found", imageId));
		});
	}

}
