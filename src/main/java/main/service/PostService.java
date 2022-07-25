package main.service;

import main.api.response.*;
import main.model.Post;

import main.model.PostComments;
import main.model.Tag;
import main.model.User;
import main.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


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

    @Autowired
    private TagRepository tagRepository;

    public CountPostsResponse getPosts(int offset, int limit, String mode) {
        LocalDateTime now = LocalDateTime.now();

        int countOfActivePosts = postRepository.getCountOfActivePost(now);
        List<Post> postList = getSortedPosts(offset, limit, mode, now);


        return convertToPostResponse(postList, offset, limit, countOfActivePosts);


    }

    public CountPostsResponse getPostsByQuery(String query, int offset, int limit)
    {

        LocalDateTime now = LocalDateTime.now();
        Pageable pagination = PageRequest.of(offset, limit);
        if(query.trim().isEmpty())
        {
            return getPosts(offset,limit,"recent");
        }
        int countOfQueryPosts = postRepository.getCountOfQueryPost(now,query);
        List<Post> postList = postRepository.getPostsByQuery(now,query,pagination).getContent();
        return convertToPostResponse(postList,offset,limit,countOfQueryPosts);
    }

    public CountPostsResponse getPostByDate(String date,int offset, int limit )
    {
        LocalDateTime now = LocalDateTime.now();

        List<Post> postList = postRepository.getActivePosts(now);
        postList = postList.stream().filter(post -> post.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(date)).collect(Collectors.toList());
        int count = postList.size();
        return convertToPostResponse(postList,offset,limit,count);
    }

    public CountPostsResponse getPostByTag(String tag,int offset, int limit){
        LocalDateTime now = LocalDateTime.now();
        Pageable pagination = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.getPostsByTag(now,tag,pagination).getContent();
        int countOfPostTags = postRepository.getCountOfPostsByTag(now,tag);
        return convertToPostResponse(postList,offset,limit,countOfPostTags);
    }

    public PostByID getPostBtId(int id)
    {
        LocalDateTime now = LocalDateTime.now();
        Post post = postRepository.getPostById(now,id);
        if(post== null){
            return null;
        }
        return convertToPostByID(post);


    }
    //TODO: прописать функционал для изменения просмотров
    private PostByID convertToPostByID(Post post)
    {
        PostByID postByID = new PostByID();
        postByID.setId(post.getId());
        postByID.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / 1000);
        byte active  = post.getIsActive();
        postByID.setActive(active == 1);
        UserResponse userResponse = new UserResponse(post.getUser().getId(),post.getUser().getName());
        postByID.setUser(userResponse);
        postByID.setTitle(post.getTitle());
        postByID.setText(post.getText());
        postByID.setLikeCount(getCountLikes(post.getId(),(byte)1));
        postByID.setDislikeCount(getCountLikes(post.getId(),(byte)-1));
        postByID.setViewCount(post.getViewCount());
        postByID.setPostComments(convertToListCommentResponse(post.getPostComments()));
        postByID.setTags(convertToTagsNamesList(post.getTagList()));
        return postByID;

    }
    //TODO:MAPPER
    private List<CommentsResponse> convertToListCommentResponse(List<PostComments> postComments){
        List<CommentsResponse> commentsResponses = new ArrayList<>();

        for(PostComments postComments1: postComments){
            int id  = postComments1.getId();
            Long timestamp  = Timestamp.valueOf(postComments1.getTime()).getTime() / 1000;
            String text = postComments1.getText();
            //TODO: ADD PHOTO TO USER
            UserResponse userResponse = new UserResponse(postComments1.getUser().getId(), postComments1.getUser().getName());
            commentsResponses.add(new CommentsResponse(id,timestamp,text,userResponse));
        }
        return commentsResponses;
    }
    //TODO:MAPPER
    private List<String> convertToTagsNamesList(List<Tag> tagList){
        List<String> tagsNamesList = new ArrayList<>();
        for (Tag tag:tagList){
            tagsNamesList.add(tag.getName());
        }
        return tagsNamesList;
    }


    //TODO:MAPPER
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
    //TODO:MAPPER
    private Integer getCountLikes(int postId, byte value) {
        Integer countLikes = 0;
        Optional<Integer> countOfLikes = postVotesRepository.findCountOfLikes(postId, value);
        if (countOfLikes.isPresent()) {
            countLikes = countOfLikes.get();
        }
        return countLikes;
    }
    //TODO:MAPPER
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
