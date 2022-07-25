package main.model.repository;


import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    /* GET api/post */
    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 ORDER BY  post.time DESC ")
    Page<Post> getSortedByRecent( LocalDateTime time,Pageable pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.postComments.size DESC ")
    Page<Post> getSortedByPopular(LocalDateTime time,Pageable pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.postVotes.size DESC ")
    Page<Post> getSortedByBest(LocalDateTime time,Pageable pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.time ASC ")
    Page<Post> getSortedByTime(LocalDateTime time, Pageable pageRequest);



    @Query("Select count(p) from Post as p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<=?1")
    Integer getCountOfActivePost(LocalDateTime time);

    /* GET api/post/search*/
    @Query("Select count(p) from Post as p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<=?1 and p.text LIKE %?2%")
    Integer getCountOfQueryPost(LocalDateTime time, String query);


    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 and post.text LIKE %?2%")
    Page<Post> getPostsByQuery(LocalDateTime time,String query,Pageable pageRequest);


    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1")
    List<Post> getActivePosts(LocalDateTime time);



    /*GET api/post/byTag*/
    @Query("select count(p) from Post as p JOIN p.tagList as tags where p.moderationStatus = 'ACCEPTED' and p.isActive = 1 and p.time<=?1 and tags.name LIKE ?2")
    Integer getCountOfPostsByTag(LocalDateTime time, String tagName);

    @Query("select post From Post as post  JOIN post.tagList as tags WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 and tags.name LIKE ?2")
    Page<Post> getPostsByTag(LocalDateTime time,String tagName,Pageable pageRequest);

    @Query("From Post as p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= ?1 and p.id = ?2")
    Post getPostById(LocalDateTime time, int id);



}
