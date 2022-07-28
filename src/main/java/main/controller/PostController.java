package main.controller;

import lombok.AllArgsConstructor;
import main.api.response.CountPostsResponse;
import main.api.response.PostByID;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/post")

/* Для реализации PostController нам нужен CountPostResponse
    Count Post Response включает в себя так же PostResponse и UserResponse.
 */
@AllArgsConstructor
public class PostController {

    private final PostService postService;


    @GetMapping("")
//    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CountPostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "10") int limit,
                                                       @RequestParam(defaultValue = "recent") String mode) {

        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/search")
//    @PreAuthorize("hasAuthority('user:moderate')")
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

    @GetMapping("/{ID}")
    public ResponseEntity<PostByID> getPostById(@PathVariable int ID, Principal principal) {
        PostByID postByID = postService.getPostBtId(ID, principal.getName());

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


}
