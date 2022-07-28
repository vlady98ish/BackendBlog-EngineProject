package main.model.repository;


import main.model.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PostCommentsRepository extends JpaRepository<PostComments, Integer> {
    @Query("SELECT count(pc) " +
            "From PostComments as pc " +
            "where pc.post.id = ?1")
    Optional<Integer> getCountOfCommentsByPostId(int postId);
}
