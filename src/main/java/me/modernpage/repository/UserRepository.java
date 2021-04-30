package me.modernpage.repository;

import me.modernpage.entity.Group;
import me.modernpage.entity.Post;
import me.modernpage.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Profile, Long> {
    List<Post> findAllHiddenPostsById(long id);
    @Query(value = "select * from profile join user_followers on profile.id = user_followers.follower_id where user_id =:userId", nativeQuery = true)
    Page<Profile> findAllFollowers(@Param("userId") long userId, Pageable page);

    @Query(value = "select * from profile join user_following on profile.id = user_following.following_id where user_id =:userId", nativeQuery = true)
    Page<Profile> findAllFollowing(@Param("userId") long userId, Pageable page);

    @Query(value = "select case when exists(select * from user_followers where user_id=:userId and follower_id=:followerId) " +
            "then 'true' else 'false' end from dual", nativeQuery = true)
    boolean existsFollower(@Param("userId") long userId, @Param("followerId") long followerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "insert into follower_request(user_id, requester_id) values(:userId, :requesterId)", nativeQuery = true)
    void addRequest(@Param("userId") long userId, @Param("requesterId") long requesterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from follower_request where user_id=:userId and requester_id=:requesterId", nativeQuery = true)
    void deleteRequest(@Param("userId") long userId, @Param("requesterId") long requesterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from user_followers where user_id=:userId and follower_id=:followerId", nativeQuery = true)
    void deleteFollower(@Param("userId") long userId, @Param("followerId") long followerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from user_following where user_id=:userId and following_id=:followingId", nativeQuery = true)
    void deleteFollowing(@Param("userId") long userId, @Param("followingId") long followingId);

    @Query(value = "select case when exists(select * from follower_request where user_id=:userId and requester_id=:requesterId) " +
            "then 'true' else 'false' end from dual", nativeQuery = true)
    boolean existsRequest(long userId, Long requesterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "insert into user_followers(user_id, follower_id) values(:userId, :followerId)", nativeQuery = true)
    void addFollower(@Param("userId") long userId, @Param("followerId") long followerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "insert into user_following(user_id, following_id) values(:userId, :followingId)", nativeQuery = true)
    void addFollowing(@Param("userId") long userId, @Param("followingId") long followingId);

    @Query(value = "select * from profile join group_members on profile.id = group_members.profile_id where group_id =:groupId", nativeQuery = true)
    Page<Profile> findAllGroupMembers(long groupId, Pageable pageable);

    List<Group> findJoinedGroupsById(long id);

//	The @Modifying annotation is used to enhance the @Query annotation to execute not only SELECT queries but also INSERT, UPDATE, DELETE, and even DDL queries.
	
//	@Modifying(clearAutomatically = true, flushAutomatically = true)
//	@Query("update Profile u set u.password = :password, u.username = :username, u.fullname = :fullname, u.birthdate = :birthdate where u.id = :userId")
//	int updatUser(@Param("userId") long userId, @Param("username") String username, @Param("fullname") String fullname, @Param("password") String password, @Param("birthdate") Date birthdate);
//	
//	@Modifying(clearAutomatically = true, flushAutomatically = true)
//	@Query("update User u set u.fullname = :fullname, u.username = :username, u.password = :password, u.birthdate = :birthdate, u.image = :image where u.id = :id")
//	int updateUser(@Param("id") long id, @Param("username") String username, @Param("fullname") String fullname, @Param("password") String password, @Param("birthdate") Date birthdate, @Param("image") String imageUri);
}
