package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostByID {
    private Integer id;

    private Long timestamp;

    private boolean active;

    private UserResponse user;

    private String title;

    private String text;

    private Integer likeCount;

    private Integer dislikeCount;

    private Integer viewCount;
    @JsonProperty("comments")
    private List<CommentsResponse> postComments;

    private List<String> tags;

}