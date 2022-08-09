package main.service;

import lombok.AllArgsConstructor;
import main.api.response.*;
import main.model.Post;
import main.model.PostComments;
import main.model.Tag;
import main.model.User;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service

public class MapperService {
    @Autowired
    private PostVotesRepository postVotesRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private TagRepository tagRepository;

    public CountPostsResponse convertToPostResponse(List<Post> postList,
                                                    int countOfActivePosts) {
        List<PostResponse> responsePostsList = new ArrayList<>();
        for (Post post : postList) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());
            postResponse.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / 1000);
            User user = post.getUser();
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            postResponse.setUser(userResponse);
            postResponse.setTitle(post.getTitle());
            String text = post.getText();
            if (text.length() > 150) {
                text = text
                        .replaceAll("<(.*?)>", "")
                        .replaceAll("[\\p{P}\\p{S}]", "")
                        .substring(0, 150) + "";
            }
            postResponse.setAnnounce(text);


            postResponse.setLikeCount(getCountLikes(post.getId(), (byte) 1));
            postResponse.setDislikeCount(getCountLikes(post.getId(), (byte) -1));
            postResponse.setCommentCount(getCountComments(post.getId()));
            postResponse.setViewCount(post.getViewCount());
            responsePostsList.add(postResponse);
        }


        CountPostsResponse countPostsResponse = new CountPostsResponse();
        countPostsResponse.setCount(countOfActivePosts);
        countPostsResponse.setPosts(responsePostsList);
        return countPostsResponse;
    }

    public PostByID convertToPostByID(Post post) {
        PostByID postByID = new PostByID();
        postByID.setId(post.getId());
        postByID.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / 1000);
        byte active = post.getIsActive();
        postByID.setActive(active == 1);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(post.getUser().getId());
        userResponse.setName(post.getUser().getName());
        postByID.setUser(userResponse);
        postByID.setTitle(post.getTitle());
        postByID.setText(post.getText());
        postByID.setLikeCount(getCountLikes(post.getId(), (byte) 1));
        postByID.setDislikeCount(getCountLikes(post.getId(), (byte) -1));
        postByID.setViewCount(post.getViewCount());
        postByID.setPostComments(convertToListCommentResponse(post.getPostComments()));
        postByID.setTags(convertToTagsNamesList(post.getTagList()));
        return postByID;

    }

    public List<CommentsResponse> convertToListCommentResponse(List<PostComments> postComments) {
        List<CommentsResponse> commentsResponses = new ArrayList<>();

        for (PostComments postComments1 : postComments) {
            int id = postComments1.getId();
            Long timestamp = Timestamp.valueOf(postComments1.getTime()).getTime() / 1000;
            String text = postComments1.getText();

            UserResponse userResponse =
                    new UserResponse();
            userResponse.setId(postComments1.getUser().getId());
            userResponse.setName(postComments1.getUser().getName());
            userResponse.setPhoto(postComments1.getUser().getPhoto());
            commentsResponses.add(new CommentsResponse(id, timestamp, text, userResponse));
        }
        return commentsResponses;
    }

    public List<Tag> convertTags(List<String> tagList) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagList) {
            Optional<Tag> optionalTag = tagRepository.findByName(tagName);
            Tag tag;
            if (optionalTag.isPresent()) {
                tag = optionalTag.get();
                tags.add(tag);
            } else {
                tag = new Tag();
                tag.setName(tagName);
                tags.add(tag);
            }
        }
        return tags;
    }

    public List<String> convertToTagsNamesList(List<Tag> tagList) {
        List<String> tagsNamesList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagsNamesList.add(tag.getName());
        }
        return tagsNamesList;
    }

    public Map<String, List<TagResponse>> convertToMapTagResponse(List<Tag> tagList, int countActivePosts) {
        List<TagResponse> tags = new ArrayList<>();
        double k = getCoefficient(tagList, countActivePosts);
        for (Tag tag : tagList) {
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(tag.getName());
            int countPostWithTag = tag.getPostList().size();
            tagResponse.setWeight(calculateWeight(countPostWithTag, countActivePosts, k));
            tags.add(tagResponse);

        }
        Map<String, List<TagResponse>> listMap = new LinkedHashMap<>();
        listMap.put("tags", tags);
        return listMap;
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

    private double calculateWeight(int countPostWithTag, int countAllPost, double coefficient) {
        double dWeightTag = (double) countPostWithTag / countAllPost;


        return dWeightTag * coefficient;
    }


    private double getCoefficient(List<Tag> tagList, int countPosts) {

        List<Integer> countOfPostsByTag = new ArrayList<>();
        for (Tag tag : tagList) {

            countOfPostsByTag.add(tag.getPostList().size());
        }
        int max = countOfPostsByTag.stream().max(Comparator.naturalOrder()).orElse(0);

        double dWeightMax = (double) max / countPosts;

        return 1 / dWeightMax;
    }
}
