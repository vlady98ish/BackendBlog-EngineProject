package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.PostRequest;
import main.api.response.CountPostsResponse;
import main.api.response.PostByID;
import main.service.LikeService;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/post")

/* Для реализации PostController нам нужен CountPostResponse
    Count Post Response включает в себя так же PostResponse и UserResponse.
 */
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;


    @GetMapping("")

    public ResponseEntity<CountPostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "10") int limit,
                                                       @RequestParam(defaultValue = "recent") String mode) {

        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/search")

    public ResponseEntity<CountPostsResponse> getPostByQuery(@RequestParam String query,
                                                             @RequestParam(defaultValue = "0") int offset,
                                                             @RequestParam(defaultValue = "6") int limit) {

        return ResponseEntity.ok(postService.getPostsByQuery(query, offset, limit));
    }

    @GetMapping("/byDate")
    public ResponseEntity<CountPostsResponse> getPostByDate(@RequestParam String date,
                                                            @RequestParam(defaultValue = "0") int offset,
                                                            @RequestParam(defaultValue = "6") int limit) {

        return ResponseEntity.ok(postService.getPostByDate(date, offset, limit));
    }

    @GetMapping("/byTag")
    public ResponseEntity<CountPostsResponse> getPostByTag(@RequestParam String tag,
                                                           @RequestParam(defaultValue = "0") int offset,
                                                           @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(postService.getPostByTag(tag, offset, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostByID> getPostById(@PathVariable int id, Principal principal) {
        String name ="";
        if(principal != null){
            name = principal.getName();
        }
        PostByID postByID = postService.getPostBtId(id, name);

        return postByID != null ? ResponseEntity.ok(postByID) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<CountPostsResponse> getModerationPosts(@RequestParam String status, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, Principal principal) {
        return ResponseEntity.ok(postService.getPostModerator(status, offset, limit, principal.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CountPostsResponse> getMy(@RequestParam String status, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, Principal principal) {
        return ResponseEntity.ok(postService.getMyPosts(status, offset, limit, principal.getName()));
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postPost(@RequestBody PostRequest postRequest, Principal principal) {
        return ResponseEntity.ok(postService.postPost(postRequest, principal.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> redactPostById(@PathVariable int id, @RequestBody PostRequest postRequest, Principal principal) {
        return ResponseEntity.ok(postService.getRedactPostById(id, postRequest, principal.getName()));
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String, Object>> like(@RequestBody Map<String, Integer> postId, Principal principal) {
        return ResponseEntity.ok(likeService.postLike(postId.get("post_id"), principal.getName()));
    }


    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String, Object>> dislike(@RequestBody Map<String, Integer> postId, Principal principal) {
        return ResponseEntity.ok(likeService.postDisLike(postId.get("post_id"), principal.getName()));
    }


}
