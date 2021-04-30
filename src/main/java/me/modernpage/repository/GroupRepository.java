package me.modernpage.repository;

import me.modernpage.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{
	Optional<List<PrivateGroup>> findGroupsByOwnerId(long ownerId);
	
	Optional<List<PrivateGroup>> findGroupsByOwner(Account owner);	
	Optional<PrivateGroup> findPrivateGroupByIdAndOwnerId(long id, long ownerId);
	
	@Query("from PrivateGroup")
	Page<PrivateGroup> findAllPrivateGroups(Pageable pageable);
	
	@Query("from PublicGroup")
	List<PublicGroup> findAllPublicGroups();
	
	@Query("select size(g.posts) from Group g where g.id =:id")
	Long findAllPostsCount(@Param("id") long groupId);
	
	@Query("select size(g.members) from PrivateGroup g where g.id =:id")
	Long findAllMembersCount(@Param("id") long privateGroupId);
	
	@Query("select g.posts from Group g where g.id =:id")
	Slice<Post> findAllPublicGroupPosts(@Param("id") long id, Pageable pageable);

	@Query(value = "select case when exists(select * from group_members where group_id=:groupId and profile_id=:userId) " +
			"then 'true' else 'false' end from dual", nativeQuery = true)
	boolean existsByGroupIdAndUserId(long groupId, long userId);

	@Modifying
	@Query(value = "insert into group_members(group_id, profile_id) values(:groupId, :userId)", nativeQuery = true)
	void addMember(@Param("groupId") long groupId, @Param("userId") long userId);

	@Modifying
	@Query(value = "delete from group_members where group_id=:groupId and profile_id=:userId", nativeQuery = true)
	void deleteMember(@Param("groupId") long groupId, @Param("userId") long userId);
}
