package main.service;

import lombok.AllArgsConstructor;
import main.api.request.CommentRequest;
import main.model.Post;
import main.model.PostComments;
import main.model.User;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {
    private final int MIN = 10;
    private final int MAX = 300;

    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> postComment(CommentRequest commentRequest, String email) {
        boolean result = true;
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();
        PostComments postComment = new PostComments();
        Integer parentId = commentRequest.getParentId();
        Integer postId = commentRequest.getPostId();
        String text = commentRequest.getText();
        if (postId == null) {
            return ResponseEntity.badRequest().body("Обьект с ключом post_id : " + postId);
        } else {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                return ResponseEntity.badRequest().body("Обьект с ключом parent_id : " + postId);
            }
            postComment.setPost(post.get());
        }
        if (parentId != null) {
            Optional<PostComments> parentComment = postCommentsRepository.findById(parentId);
            if (parentComment.isEmpty()) {
                return ResponseEntity.badRequest().body("Обьект с ключом parent_id : " + parentId);
            }
            postComment.setParentComment(parentComment.get());
        }

        if (checkText(text)) {
            result = false;
            response.put("result", result);
            errors.put("text", "Текст комментария не задан или слишком короткий");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }
        User user = userRepository.findUserByEmail(email).get();
        postComment.setUser(user);
        postComment.setTime(LocalDateTime.now());
        postComment.setText(text);
        postComment = postCommentsRepository.save(postComment);
        return ResponseEntity.ok(Map.of("id", postComment.getId()));


    }

    private boolean checkText(String text) {
        return text.length() < MIN || text.length() > MAX;
    }

}
