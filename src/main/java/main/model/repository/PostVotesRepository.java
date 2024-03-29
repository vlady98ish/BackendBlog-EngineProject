package main.model.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVote,Integer> {
    @Query("Select count(pv) " +
            "from PostVote as pv " +
            "where pv.post.id = ?1 " +
            "and pv.value =?2")
    Optional<Integer> findCountOfLikes(int postId, byte value);

    PostVote findByUserAndPost(User user, Post post);


    int countPostVoteByValue(byte value);

}
