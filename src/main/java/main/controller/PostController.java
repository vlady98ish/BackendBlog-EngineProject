package main.controller;

import main.api.response.CountPostsResponse;
import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

/* Для реализации PostController нам нужен CountPostResponse
    Count Post Response включает в себя так же PostResponse и UserResponse.
 */
public class PostController {

    private final PostService postService;

    public PostController(PostService postService)
    {
        this.postService = postService;
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit,@RequestParam(defaultValue = "recent") String mode)
    {
        System.out.println("We are in PostController getPosts");
        return postService.getPosts(offset,limit,mode);
    }
}
