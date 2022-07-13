package main.service;

import main.api.response.CountPostsResponse;
import main.api.response.PostResponse;
import main.api.response.UserResponse;
import main.model.Post;

import main.model.User;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.*;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVotesRepository postVotesRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;

    public ResponseEntity<?> getPosts(int offset, int limit, String mode) {
        if (offset > limit) {
            return new ResponseEntity<>("Wrong offset parametr", HttpStatus.BAD_REQUEST);
        }

        List<Post> postList = getLimitAndSortedPosts(offset, limit, mode);
        List<PostResponse> responsePostsList = new ArrayList<>();
        for (Post post : postList) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());
            postResponse.setTimestamp(post.getTime().getTime() / 1000);
            User user = post.getUser();

            postResponse.setUser(new UserResponse(user.getId(), user.getName()));
            postResponse.setTitle(post.getTitle());
            postResponse.setAnnounce(post.getText().replaceAll("<(.*?)>", "").replaceAll("[\\p{P}\\p{S}]", ""));
            postResponse.setLikeCount(getCountLikes(post.getId(),1));
            postResponse.setDislikeCount(getCountLikes(post.getId(), -1));
            postResponse.setCommentCount(getCountComments(post.getId()));
            postResponse.setViewCount(post.getViewCount());
            responsePostsList.add(postResponse);
        }


        CountPostsResponse countPostsResponse = new CountPostsResponse();
        countPostsResponse.setCount(postList.size());
        countPostsResponse.setPosts(responsePostsList);


        return new ResponseEntity<>(countPostsResponse, HttpStatus.OK);


    }

    private Integer getCountLikes(int postId, int value) {
        Integer countLikes = 0;
        Optional<Integer> countOfLikes = postVotesRepository.findCountOfLikes(postId, value);
        if (countOfLikes.isPresent()) {
            countLikes = countOfLikes.get();
        }
        return countLikes;
    }

    private Integer getCountComments(int postId)
    {
        Integer countComments = 0;
        Optional<Integer> countOfComments = postCommentsRepository.getCountOfCommentsByPostId(postId);
        if(countOfComments.isPresent())
        {
            countComments = countOfComments.get();
        }
        return countComments;
    }

    public List<Post> getLimitAndSortedPosts(int offset, int limit, String mode) {
        PageRequest pagination = PageRequest.of(offset, limit);
        Page<Post> posts;
        switch (mode) {
            case "popular":
                posts = postRepository.getSortedByPopular(pagination);
                break;
            case "best":
                posts = postRepository.getSortedByBest(pagination);
                break;
            case "early":
                posts = postRepository.getSortedByTime(pagination);
            default:
                posts = postRepository.getSortedByRecent(pagination);

        }

        return posts.getContent();
    }
}
