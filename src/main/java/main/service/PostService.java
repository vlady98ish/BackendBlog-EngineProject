package main.service;

import lombok.AllArgsConstructor;

import main.api.request.ModeratorDecisionRequest;
import main.api.request.PostRequest;
import main.api.response.*;
import main.model.*;

import main.model.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;



import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service

public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private MapperService mapperService;


    public CountPostsResponse getPosts(int offset, int limit, String mode) {
        LocalDateTime now = LocalDateTime.now();


        Page<Post> postPage = getSortedPosts(offset, limit, mode, now);
        int countOfActivePosts = (int) postPage.getTotalElements();


        return mapperService.convertToPostResponse(postPage.getContent(), countOfActivePosts);


    }

    public CountPostsResponse getPostsByQuery(String query, int offset, int limit) {

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (query.trim().isEmpty()) {
            return getPosts(offset, limit, "recent");
        }

        Page<Post> postList = postRepository.getPostsByQuery(now, query, pageable);
        int countOfQueryPosts = (int) postList.getTotalElements();
        return mapperService.convertToPostResponse(postList.getContent(), countOfQueryPosts);
    }

    public CountPostsResponse getPostByDate(String date, int offset, int limit) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPage = postRepository.getActivePostsPage(now, pageable);
        List<Post> postList = postPage.getContent();
        postList = postList
                .stream()
                .filter(post -> post.getTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .equals(date))
                .collect(Collectors.toList());
        int count = (int) postPage.getTotalElements();
        return mapperService.convertToPostResponse(postList, count);
    }

    public CountPostsResponse getPostByTag(String tag, int offset, int limit) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pagination = PageRequest.of(offset / limit, limit);
        List<Post> postList = postRepository.getPostsByTag(now, tag, pagination).getContent();
        int countOfPostTags = postRepository.getCountOfPostsByTag(now, tag);
        return mapperService.convertToPostResponse(postList, countOfPostTags);
    }


    //TODO: Почему выбивает ошибку если юзер не зареган
    public PostByID getPostBtId(int id, String email) {
        LocalDateTime now = LocalDateTime.now();
        Post post = postRepository.getPostByIdAndTime(now, id);
        if (post == null) {
            return null;
        }
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            User sessionUser = user.get();
            if (sessionUser.getIsModerator() == 0) {
                if (sessionUser.getId() != post.getUser().getId()) {
                    post.setViewCount(post.getViewCount() + 1);
                }
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }


        postRepository.save(post);

        return mapperService.convertToPostByID(post);


    }

    public CountPostsResponse getPostModerator(String status, int offset, int limit, String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        User sessionUser;
        if (user.isPresent()) {
            sessionUser = user.get();
        } else {
            return null;
        }


        Page<Post> postList = getPostsByModeratingStatus(offset, limit, status, sessionUser.getId());
        int countOfPosts = (int) postList.getTotalElements();
        return mapperService.convertToPostResponse(postList.getContent(), countOfPosts);
    }


    public CountPostsResponse getMyPosts(String status, int offset, int limit, String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        User sessionUser;
        if (user.isPresent()) {
            sessionUser = user.get();
        } else {
            return null;
        }

        Page<Post> postList = getPostsByUserStatus(offset, limit, status, sessionUser.getId());
        int count = (int) postList.getTotalElements();
        return mapperService.convertToPostResponse(postList.getContent(), count);
    }

    public Map<String, Object> postPost(PostRequest postRequest, String email) {
        Post post = new Post();
        User sessionUser = userRepository.findUserByEmail(email).get();

        Map<String, Object> fullResponse = isValidTextAndTitle(postRequest.getText(), postRequest.getTitle());
        if (((boolean) fullResponse.get("result"))) {

            postRepository.save(buildPost(postRequest, post, sessionUser, mapperService.convertTags(postRequest.getTags())));
        }

        return fullResponse;

    }


    public Map<String, Object> getRedactPostById(int id, PostRequest postRequest, String email) {


        User sessionUser = userRepository.findUserByEmail(email).get();
        Post post = postRepository.getPostById(id);
        Map<String, Object> fullResponse = isValidTextAndTitle(postRequest.getText(), postRequest.getTitle());
        if ((boolean) fullResponse.get("result")) {
            postRepository.save(buildPost(postRequest, post, sessionUser, mapperService.convertTags(postRequest.getTags())));
        }
        return fullResponse;
    }

    public Map<String, Object> editPostStatus(ModeratorDecisionRequest moderatorDecisionRequest, String email) {
        Post post = postRepository.getPostById(moderatorDecisionRequest.getPostId());
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        String decision = moderatorDecisionRequest.getDecision();
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return Map.of("result", false);
        }
        if (post != null) {
            if (decision.equals("accept")) {
                post.setModerationStatus(Status.ACCEPTED);
                post.setModeratedBy(user);
                postRepository.save(post);
            } else if (decision.equals("decline")) {
                post.setModerationStatus(Status.DECLINED);
                post.setModeratedBy(user);
                postRepository.save(post);
            }
        }
        return Map.of("result", true);
    }

    private Map<String, Object> isValidTextAndTitle(String text, String title) {

        Map<String, Object> fullResponse = new LinkedHashMap<>();
        boolean result = true;
        Map<String, String> errorResponse = new LinkedHashMap<>();
        if (title.isEmpty()) {
            result = false;
            errorResponse.put("title", "Заголовок не установлен");
        } else if (title.length() < 3) {
            result = false;
            errorResponse.put("title", "Заголовок слишком короткий");
        }

        if (text.isEmpty()) {
            result = false;
            errorResponse.put("text", "Текст не установлен");
        } else if (text.length() < 50) {
            result = false;
            errorResponse.put("text", "Текст слишком короткий");
        }
        fullResponse.put("result", result);
        if (!result) {
            fullResponse.put("errors", errorResponse);
        }
        return fullResponse;
    }


    private Post buildPost(PostRequest postRequest, Post post, User user, List<Tag> tags) {
        Post newPost = post == null ? new Post() : post;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(postRequest.getTimestamp()),
                TimeZone.getDefault().toZoneId());
        LocalDateTime now = LocalDateTime.now();
        if (localDateTime.isBefore(now)) {
            localDateTime = now;
        }
        newPost.setTime(localDateTime);
        newPost.setIsActive((byte) postRequest.getActive());
        newPost.setTitle(postRequest.getTitle());
        newPost.setText(postRequest.getText());
        if (newPost.getId() == 0) {
            newPost.setUser(user);
        }
        if (post == null || ((user.getIsModerator() == 0) && newPost.getUser().equals(user))) {
            if (!settingsService.getGlobalSettings().isPostPreModeration()) {
                newPost.setModerationStatus(Status.ACCEPTED);
            } else {
                newPost.setModerationStatus(Status.NEW);
            }
        }


        newPost.setTagList(tags);

        return post;
    }


    private Page<Post> getPostsByUserStatus(int offset, int limit, String status, int id) {

        Pageable pageable = PageRequest.of(offset / limit, limit);

        if (status.equals("inactive")) {
            return postRepository.getMyNotActivePosts(id, pageable);
        }
        String stat = status.equals("pending") ? "NEW" : status;
        return postRepository.getMyActivePosts(stat, id, pageable);
    }


    private Page<Post> getPostsByModeratingStatus(int offset, int limit, String status, int id) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> posts;
        switch (status) {


            case "accepted":
                posts = postRepository.getPostByMeAccepted(id, pageable);
                break;
            case "declined":
                posts = postRepository.getPostByMeDeclined(id, pageable);
                break;
            default:
                posts = postRepository.getPostModerator(pageable);
                break;


        }

        return posts;
    }


    private Page<Post> getSortedPosts(int offset, int limit, String mode, LocalDateTime time) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
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

        return posts;
    }


}
