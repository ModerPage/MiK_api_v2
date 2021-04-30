package me.modernpage.aspect;//package me.modernpage.aspect;

import me.modernpage.common.PageStoreClass;
import me.modernpage.controller.*;
import me.modernpage.entity.*;
import me.modernpage.response.FollowPage;
import me.modernpage.response.LikePage;
import me.modernpage.response.MemberPage;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@Aspect
public class LinkAdditionAspect {


	@AfterReturning(pointcut = "allService()", returning = "page")
    public void addPostPageLink(JoinPoint joinPoint, Page<Post> page) {
		System.out.println("addPostPageLink");
		for(Post post : page.getContent()) {
			postLinks(post);
			if(post.getOwner().getLinks().isEmpty())
				profileLinks(post.getOwner());
			if(post.getGroup().getLinks().isEmpty())
				groupLinks(post.getGroup());
		}
		PageStoreClass.page.set(page);
    }

	@AfterReturning(pointcut = "allService()", returning = "likePage")
	public void addLikePageLink(JoinPoint joinPoint, LikePage<Like> likePage) {
		for(Like like: likePage.getContent()) {
			profileLinks(like.getOwner());
		}
		PageStoreClass.page.set(likePage);
	}

	@AfterReturning(pointcut = "allService()", returning = "memberPage")
	public void addMemberPageLink(JoinPoint joinPoint, MemberPage<Profile> memberPage) {
		for(Profile profile: memberPage.getContent()) {
			if(profile.getLinks().isEmpty()) {
				profileLinks(profile);
			}
		}
		PageStoreClass.page.set(memberPage);
	}

	@AfterReturning(pointcut = "allService()", returning = "commentPage")
	public void addCommentPageLink(JoinPoint joinPoint, Page<Comment> commentPage) {
		for(Comment comment: commentPage.getContent()) {
			if(comment.getOwner().getLinks().isEmpty())
				profileLinks(comment.getOwner());
		}
		PageStoreClass.page.set(commentPage);
	}

	@AfterReturning(pointcut = "allService()", returning = "userPage")
	public void addUserPageLink(JoinPoint joinPoint, Page<Profile> userPage) {
		for(Profile profile: userPage.getContent()) {
			if(profile.getLinks().isEmpty())
				profileLinks(profile);
		}
		PageStoreClass.page.set(userPage);
	}

	@AfterReturning(pointcut="allService()", returning = "post")
	public void addPostLink(Post post) {
		postLinks(post);
//		profileLinks(post.getOwner());
//		groupLinks(post.getGroup());
	}

	@AfterReturning(pointcut="allService()", returning = "posts")
	public void addPostsLink(List<Post> posts) {
		for(Post post: posts) {
			postLinks(post);
			if(post.getOwner().getLinks().isEmpty())
				profileLinks(post.getOwner());
			if(post.getGroup().getLinks().isEmpty())
				groupLinks(post.getGroup());
		}
	}

	@AfterReturning(pointcut = "allService()", returning = "followers")
	public void addFollowersLink(FollowPage<Profile> followers) {
		for(Profile profile: followers.getContent()) {
			if(profile.getLinks().isEmpty()) {
				profileLinks(profile);
			}
		}
		PageStoreClass.page.set(followers);
	}

	@AfterReturning(pointcut = "allService()", returning = "privateGroups")
	public void addPrivateGroupsLinks(Page<PrivateGroup> privateGroups) {
		for(PrivateGroup group: privateGroups.getContent()) {
			if(group.getLinks().isEmpty()) {
				groupLinks(group);
			}
		}
		PageStoreClass.page.set(privateGroups);
	}

	@AfterReturning(pointcut = "allService()", returning = "account")
	public void addUserLink(Optional<Account> account) {
        account.ifPresent(value -> profileLinks(value.getProfile()));
	}

	@AfterReturning(pointcut = "allService()", returning = "profile")
	public void addProfileLink(Optional<Profile> profile) {
		profile.ifPresent(this::profileLinks);
	}

//	@AfterReturning(value = "execution(me.modernpage.entity.Profile updateAccount(..))", returning = "profile")
//	public void addProfileLinkAfterUpdate(Profile profile) {
//		System.out.println("add link");
////		profile.ifPresent(this::profileLinks);
//		profileLinks(profile);
//	}
	@AfterReturning(pointcut = "allService()", returning = "group")
	public void addGroupLink(Group group) {
		groupLinks(group);
	}

	@AfterReturning(pointcut = "allService()", returning = "groups")
	public void addGroupsLink(List<Group> groups) {
		for(Group group: groups)
			groupLinks(group);
	}

	@AfterReturning(pointcut ="allService()", returning = "privateGroups")
	public void addPrivateGroupListLink(List<PrivateGroup> privateGroups) {
		for(PrivateGroup group: privateGroups)
			groupLinks(group);

//		if(!privateGroups.isEmpty())
//			userLinks(privateGroups.get(0).getOwner());
	}

	@AfterReturning(pointcut ="allService()", returning = "publicGroups")
	public void addPublicGroupListLink(List<PublicGroup> publicGroups) {
		for(PublicGroup group: publicGroups)
			groupLinks(group);
	}

//	@Pointcut("within(me.modernpage.controller.PostResource)")
//	public void allPostResource() {}
//
//	@Pointcut("within(me.modernpage.controller.UserResource)")
//	public void allUserResource() {}
//
//	@Pointcut("within(me.modernpage.controller.GroupResource)")
//	public void allGroupResource() {}

	@Pointcut("within(me.modernpage.service..*)")
	public void allService() {}


	private void postLinks(Post post) {
		Link selfLink = linkTo(PostResource.class).slash(post.getId()).withSelfRel();
		Link likeLink = linkTo(PostResource.class).slash(post.getId()).slash("likes").withRel("likes");
		Link commentLink = linkTo(PostResource.class).slash(post.getId()).slash("comments").withRel("comments");
		Link locationLink = linkTo(PostResource.class).slash(post.getId()).slash("location").withRel("location");
		Link groupLink = null;
		if(post.getGroup() instanceof PrivateGroup)
			groupLink = linkTo(PrivateGroupResource.class).slash(post.getGroup().getId()).withRel("group");
		else
			groupLink = linkTo(PublicGroupResource.class).slash(post.getGroup().getId()).withRel("group");
		String fileExt = FilenameUtils.getExtension(post.getFile().getLocation());
		Link fileLink = linkTo(PostResource.class).slash(post.getId()).slash("files").slash(post.getFile().getId())
					.slash(post.getFile().getName() + "." + fileExt).withRel("file");
		Link ownerLink = linkTo(UserResource.class).slash(post.getOwner().getId()).withRel("owner");
		post.add(selfLink).add(fileLink).add(ownerLink).add(locationLink).add(groupLink).add(likeLink).add(commentLink);
	}

	private void groupLinks(Group group) {
		Link selfLink = null, imageLink = null, postsLink = null;
		String fileExt = FilenameUtils.getExtension(group.getImage().getLocation());
		if(group instanceof PrivateGroup) {
			selfLink = linkTo(PrivateGroupResource.class).slash(group.getId()).withSelfRel();
			Link ownerLink = linkTo(UserResource.class).slash(((PrivateGroup)group).getOwner().getId()).withRel("owner");
			group.add(ownerLink);
			Link membersLink = linkTo(PrivateGroupResource.class).slash(group.getId()).slash("members").withRel("members");
			group.add(membersLink);
			postsLink = linkTo(PrivateGroupResource.class).slash(group.getId()).slash("posts").withRel("posts");
			imageLink = linkTo(PrivateGroupResource.class).slash(group.getId()).slash("images").slash(group.getImage().getId())
					.slash(group.getImage().getName() + "." + fileExt).withRel("image");
		} else {
			selfLink = linkTo(PublicGroupResource.class).slash(group.getId()).withSelfRel();
			postsLink = linkTo(PublicGroupResource.class).slash(group.getId()).slash("posts").withRel("posts");
			imageLink = linkTo(PublicGroupResource.class).slash(group.getId()).slash("images").slash(group.getImage().getId())
					.slash(group.getImage().getName() + "." + fileExt).withRel("image");
		}
		group.add(imageLink).add(selfLink).add(postsLink);
	}

	private void profileLinks(Profile profile) {
		Link selfLink = linkTo(UserResource.class).slash(profile.getId()).withSelfRel();
		Link group = linkTo(UserGroupResource.class).slash(profile.getId()).slash("groups").withRel("groups");
		Link post = linkTo(UserPostResource.class).slash(profile.getId()).slash("posts").withRel("posts");
		Link hidden_posts = linkTo(UserPostResource.class).slash(profile.getId()).slash("posts").slash("?state=hidden").withRel("hidden_posts");
		String fileExt = FilenameUtils.getExtension(profile.getImage().getLocation());
		Link avatar = linkTo(UserResource.class).slash(profile.getId()).slash("images").slash(profile.getImage().getId())
				.slash(profile.getImage().getName() + "." + fileExt).withRel("avatar");
		Link follower = linkTo(UserResource.class).slash(profile.getId()).slash("followers").withRel("followers");
		Link following = linkTo(UserResource.class).slash(profile.getId()).slash("following").withRel("following");
		profile.add(selfLink).add(avatar).add(group).add(post).add(hidden_posts).add(follower).add(following);
	}
}
