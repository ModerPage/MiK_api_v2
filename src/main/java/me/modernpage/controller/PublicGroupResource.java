package me.modernpage.controller;//package me.modernpage.controller;

import me.modernpage.entity.Post;
import me.modernpage.service.GroupManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publicgroups")
public class PublicGroupResource {
	@Autowired
	private GroupManager groupManager;


	@GetMapping(produces = "application/json")
	public ResponseUtils getPublicGroups() {
		return ResponseUtils.success(groupManager.getAllPublicGroups());
	}

	@GetMapping(value="/{groupId:[0-9]+}", produces="application/json")
	public ResponseUtils getPublicGroup(@PathVariable long groupId) {
		return groupManager.findGroupById(groupId).map(ResponseUtils::success).orElseGet(() ->
		    ResponseUtils.response(500,String.format("No public group found with id %d", groupId), null));
	}

	@GetMapping(value="/{groupId:[0-9]+}/posts")
	public ResponseUtils getPublicGroupPosts(@PathVariable long groupId, @RequestParam(defaultValue = "0",required = false) int page, @RequestParam(defaultValue = "5", required = false) int size) {
		Page<Post> result = groupManager.getAllPostsByGroupId(groupId, PageRequest.of(page, size));
		return ResponseUtils.success(result);
	}

	@GetMapping(value = "/{groupId:[0-9]+}/images/{imageId:[0-9]+}/**")
	public FileSystemResource getImage(@PathVariable long groupId, @PathVariable long imageId) {
		return groupManager.findGroupImageById(imageId).<IllegalStateException>orElseThrow(()->{
			throw new IllegalStateException(String.format("Image with %d id not found", imageId));
		});
	}
}
