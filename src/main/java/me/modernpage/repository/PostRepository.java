package me.modernpage.repository;

import me.modernpage.entity.Account;
import me.modernpage.entity.Group;
import me.modernpage.entity.Location;
import me.modernpage.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	Optional<Location> findLocationById(long postId);
	Page<Post> findAllByOwnerId(long ownerId, Pageable page);

	
	@Query("select size(p.likes) from Post p where p.id =:id")
	Long findAllPostLikesCount(@Param("id") long postId);
	
	@Query("select size(p.comments) from Post p where p.id =:id")
	Long findAllPostCommentsCount(@Param("id") long postId);

	@Modifying
	@Query(value = "insert into hidden_posts(user_id, post_id) values(:user_id, :post_id)", nativeQuery = true)
	void hidePost(@Param("user_id") long user_id, @Param("post_id") long post_id);

	@Query(value = "select * from post where post.group_id =:groupId and post.id not in " +
			"(select hidden_posts.post_id from hidden_posts where hidden_posts.user_id =:userId) order by post.created desc ", nativeQuery = true)
	Page<Post> findAllByGroupId(@Param("groupId") long groupId, @Param("userId") long userId, Pageable page);
	Optional<Account> findOwnerById(long postId);
	Optional<Group> findGroupById(long postId);

	@Query(value = "select * from post where post.group_id in (\n" +
			"select public_group.id from public_group union select group_members.group_id from group_members where group_members.profile_id =:userId) and post.id not in (\n" +
			"select hidden_posts.post_id from hidden_posts where hidden_posts.user_id =:userId) order by post.created desc ", nativeQuery = true)
	Page<Post> findAll(long userId, Pageable page);
}
