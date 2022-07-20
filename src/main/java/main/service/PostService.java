package main.service;

import main.api.response.CountPostsResponse;
import main.api.response.PostResponse;
import main.api.response.UserResponse;
import main.model.Post;

import main.model.User;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVotesRepository postVotesRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private UserRepository userRepository;

    public CountPostsResponse getPosts(int offset, int limit, String mode) {
        LocalDateTime now = LocalDateTime.now();
        if (offset > limit) {
            CountPostsResponse countPostsResponse = new CountPostsResponse();
            countPostsResponse.setCount(0);
        }
        int countOfActivePosts = postRepository.getCountOfActivePost(now);
        List<Post> postList = getSortedPosts(offset, limit, mode, now);


        return convertToPostResponse(postList, offset, limit, countOfActivePosts);


    }

    public CountPostsResponse getPostsByQuery(String query, int offset, int limit)
    {
        LocalDateTime now = LocalDateTime.now();
        PageRequest pagination = PageRequest.of(offset, limit);
        if(query.trim().isEmpty())
        {
            return getPosts(offset,limit,"recent");
        }
        int countOfQueryPosts = postRepository.getCountOfQueryPost(now,query);
        List<Post> postList = postRepository.getPostsByQuery(now,query,pagination).getContent();
        return convertToPostResponse(postList,offset,limit,countOfQueryPosts);
    }

    private CountPostsResponse convertToPostResponse(List<Post> postList, int offset, int limit, int countOfActivePosts) {
        List<PostResponse> responsePostsList = new ArrayList<>();
        for (Post post : postList) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());
            postResponse.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / 1000);
            User user = post.getUser();

            postResponse.setUser(new UserResponse(user.getId(), user.getName()));
            postResponse.setTitle(post.getTitle());
            postResponse.setAnnounce(post.getText().replaceAll("<(.*?)>", "").replaceAll("[\\p{P}\\p{S}]", "").substring(0, 150) + "...");


            postResponse.setLikeCount(getCountLikes(post.getId(), (byte) 1));
            postResponse.setDislikeCount(getCountLikes(post.getId(), (byte) -1));
            postResponse.setCommentCount(getCountComments(post.getId()));
            postResponse.setViewCount(post.getViewCount());
            responsePostsList.add(postResponse);
        }


        CountPostsResponse countPostsResponse = new CountPostsResponse();
        countPostsResponse.setCount(countOfActivePosts);
        countPostsResponse.setPosts(getLimitOffsetPost(responsePostsList, offset, limit));
        return countPostsResponse;
    }

    private Integer getCountLikes(int postId, byte value) {
        Integer countLikes = 0;
        Optional<Integer> countOfLikes = postVotesRepository.findCountOfLikes(postId, value);
        if (countOfLikes.isPresent()) {
            countLikes = countOfLikes.get();
        }
        return countLikes;
    }

    private Integer getCountComments(int postId) {
        Integer countComments = 0;
        Optional<Integer> countOfComments = postCommentsRepository.getCountOfCommentsByPostId(postId);
        if (countOfComments.isPresent()) {
            countComments = countOfComments.get();
        }
        return countComments;
    }

    private List<Post> getSortedPosts(int offset, int limit, String mode, LocalDateTime time) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Post> posts;
        switch (mode) {
            case "popular":
                posts = postRepository.getSortedByPopular(time, pageable);
                break;
            case "best":
                posts = postRepository.getSortedByBest(time, pageable);
                break;
            case "early":
                posts = postRepository.getSortedByTime(time, pageable);
                break;
            default:
                posts = postRepository.getSortedByRecent(time, pageable);

        }

        return posts.getContent();
    }

    private List<PostResponse> getLimitOffsetPost(List<PostResponse> postList, int offset, int limit) {
        List<PostResponse> limitedListPosts = new ArrayList<>();
        if (offset > limit || offset > postList.size()) {
            return limitedListPosts;
        } else if (limit + offset <= postList.size()) {
            limitedListPosts = postList.subList(offset, offset + limit);
            return limitedListPosts;
        }
        limitedListPosts = postList.subList(offset, postList.size());
        return limitedListPosts;

    }
}
