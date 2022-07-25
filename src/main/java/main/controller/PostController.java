package main.controller;

import main.api.response.CountPostsResponse;
import main.api.response.PostByID;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")

/* Для реализации PostController нам нужен CountPostResponse
    Count Post Response включает в себя так же PostResponse и UserResponse.
 */
public class PostController {

    private final PostService postService;

    public PostController(PostService postService)
    {
        this.postService = postService;
    }

    @GetMapping("/")
    public ResponseEntity<CountPostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "recent") String mode)
    {

        return ResponseEntity.ok(postService.getPosts(offset,limit,mode));
    }

    @GetMapping("/search")
    public ResponseEntity<CountPostsResponse> getPostByQuery(@RequestParam String query,@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "6") int limit)
    {

        return ResponseEntity.ok(postService.getPostsByQuery(query,offset,limit));
    }
    @GetMapping("/byDate")
        public ResponseEntity<CountPostsResponse> getPostByDate(@RequestParam String date,@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "6") int limit)
        {

            return ResponseEntity.ok(postService.getPostByDate(date,offset,limit));
        }

    @GetMapping("/byTag")
    public ResponseEntity<CountPostsResponse> getPostByTag(@RequestParam String tag,@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit){

        return ResponseEntity.ok(postService.getPostByTag(tag,offset,limit));
    }

    @GetMapping("/{ID}")
    public ResponseEntity<PostByID> getPostById(@PathVariable int ID){
        PostByID postByID = postService.getPostBtId(ID);

        return postByID != null? ResponseEntity.ok(postByID) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
