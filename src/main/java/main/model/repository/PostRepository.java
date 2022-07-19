package main.model.repository;


import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 ORDER BY  post.time DESC ")
    Page<Post> getSortedByRecent( LocalDateTime time,PageRequest pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.postComments.size DESC ")
    Page<Post> getSortedByPopular(LocalDateTime time,PageRequest pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.postVotes.size DESC ")
    Page<Post> getSortedByBest(LocalDateTime time,PageRequest pageRequest);

    @Query("From Post as post WHERE post.isActive = 1 and post.moderationStatus = 'ACCEPTED' and post.time<=?1 order by post.time ASC ")
    Page<Post> getSortedByTime(LocalDateTime time,PageRequest pageRequest);


    //TODO: Добпвить проверку на время
    @Query("Select count(p) from Post as p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<=?1")
    Integer getCountOfActivePost(LocalDateTime time);

}
