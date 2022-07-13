package main.model.repository;


import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("From Post as post ORDER BY  post.time DESC ")
    Page<Post> getSortedByRecent(PageRequest pageRequest);

    @Query("From Post as post order by post.postComments.size DESC ")
    Page<Post> getSortedByPopular(PageRequest pageRequest);

    @Query("From Post as post order by post.postVotes.size DESC ")
    Page<Post> getSortedByBest(PageRequest pageRequest);

    @Query("From Post as post order by post.time ASC ")
    Page<Post> getSortedByTime(PageRequest pageRequest);

}
