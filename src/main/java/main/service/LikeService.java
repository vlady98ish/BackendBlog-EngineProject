package main.service;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class LikeService {


    @Autowired
    private PostVotesRepository postVotesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public Map<String, Object> postLike(Integer post_id, String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        Post post = postRepository.getOne(post_id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            PostVote postVote = postVotesRepository.findByUserAndPost(user, post);
            if (postVote != null) {
                if (postVote.getValue() == 1) {
                    return Map.of("result", false);
                } else if (postVote.getValue() == -1) {
                    postVote.setValue((byte) 1);
                    postVotesRepository.save(postVote);
                    return Map.of("result", true);
                }
            }
            postVote = new PostVote();
            postVote.setUser(user);
            postVote.setPost(post);
            postVote.setTime(LocalDateTime.now());
            postVote.setValue((byte) 1);
            postVotesRepository.save(postVote);
            return Map.of("result", true);
        }

        return Map.of("result", false);

    }

    public Map<String, Object> postDisLike(Integer post_id, String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        Post post = postRepository.getOne(post_id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            PostVote postVote = postVotesRepository.findByUserAndPost(user, post);
            if (postVote != null) {
                if (postVote.getValue() == -1) {
                    return Map.of("result", false);
                } else if (postVote.getValue() == 1) {
                    postVote.setValue((byte) -1);
                    postVotesRepository.save(postVote);
                    return Map.of("result", true);
                }
            }
            postVote = new PostVote();
            postVote.setUser(user);
            postVote.setPost(post);
            postVote.setTime(LocalDateTime.now());
            postVote.setValue((byte) -1);
            postVotesRepository.save(postVote);
            return Map.of("result", true);
        }

        return Map.of("result", false);

    }


}

