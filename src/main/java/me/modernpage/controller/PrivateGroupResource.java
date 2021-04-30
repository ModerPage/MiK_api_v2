package me.modernpage.controller;//package me.modernpage.controller;

import me.modernpage.service.GroupManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/privategroups")
public class PrivateGroupResource {

	@Autowired
    private
    GroupManager groupManager;

	@GetMapping( produces = "application/json")
	public ResponseUtils getPrivateGroups(@RequestParam(defaultValue = "0",required = false) int page,
                                          @RequestParam(defaultValue = "5", required = false) int size) {
	    return ResponseUtils.success(groupManager.findAllPrivateGroups(PageRequest.of(page, size)));
	}

    @GetMapping(value = "/{groupId:[0-9]+}/images/{imageId:[0-9]+}/**")
    public FileSystemResource getImage(@PathVariable long groupId, @PathVariable long imageId) {
        return groupManager.findGroupImageById(imageId).<IllegalStateException>orElseThrow(()->{
            throw new IllegalStateException(String.format("Image with %d id not found", imageId));
        });
    }

    @GetMapping(value = "/{groupId:[0-9]+}/members")
    public ResponseUtils getMembers(@PathVariable long groupId,
                                    @RequestParam(defaultValue = "0",required = false) int page,
                                    @RequestParam(defaultValue = "5", required = false) int size,
                                    @RequestParam long userId) {
	    return ResponseUtils.success(groupManager.findAllMembers(groupId, userId, PageRequest.of(page, size)));
    }

    @PostMapping(value = "/{groupId:[0-9]+}/members")
    public ResponseUtils addMembers(@PathVariable long groupId, @RequestParam long userId) {
	    groupManager.addMember(groupId, userId);
	    return ResponseUtils.success(null);
    }

    @GetMapping(value = "/{groupId:[0-9]+}/posts")
    public ResponseUtils getPosts(@PathVariable long groupId,
                                    @RequestParam(defaultValue = "0",required = false) int page,
                                    @RequestParam(defaultValue = "5", required = false) int size) {
        return ResponseUtils.success(groupManager.findAllPosts(groupId, PageRequest.of(page, size)));
    }

    @DeleteMapping(value = "/{groupId:[0-9]+}")
    public ResponseUtils deleteGroup(@PathVariable long groupId) {
	    groupManager.deleteGroup(groupId);
	    return ResponseUtils.success(null);
    }

    @GetMapping(value = "/{groupId:[0-9]+}")
    public ResponseUtils checkGroupJoin(@PathVariable long groupId, @RequestParam long userId) {
	    return ResponseUtils.success(groupManager.checkJoin(groupId, userId));
    }

    @DeleteMapping(value = "/{groupId:[0-9]+}/members")
    public ResponseUtils deleteMember(@PathVariable long groupId, @RequestParam long userId) {
	    groupManager.deleteMember(groupId, userId);
	    return ResponseUtils.success(null);
    }
}
