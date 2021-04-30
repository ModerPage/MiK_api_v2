package me.modernpage.controller;//package me.modernpage.controller;


import me.modernpage.entity.Group;
import me.modernpage.entity.PrivateGroup;
import me.modernpage.service.GroupManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserGroupResource {

	@Autowired
	private GroupManager groupManager;

	@GetMapping(value = "/{userId:[0-9]+}/groups", produces = "application/json")
	public ResponseUtils getGroups(@PathVariable("userId") long userId) {
		List<Group> groupsByOwnerId = groupManager.findGroupsByUserId(userId);
		return ResponseUtils.success(groupsByOwnerId);
	}

	@PostMapping(value="/{userId:[0-9]+}/groups", consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseUtils saveGroup(@PathVariable("userId") long userId, @RequestBody PrivateGroup group) {
		return ResponseUtils.success(groupManager.saveGroup(userId, group));
	}

	@PostMapping(value="/{userId:[0-9]+}/groups", consumes = "multipart/form-data", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseUtils saveGroup(@PathVariable("userId") long userId, @RequestPart PrivateGroup group, @RequestParam MultipartFile file) throws IOException {
		return ResponseUtils.success(groupManager.saveGroup(userId, group, file));
	}
}
