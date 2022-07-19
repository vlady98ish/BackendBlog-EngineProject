package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.sql.Timestamp;
/*Post Response для CountPostResponse*/


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Integer id;

    private Long timestamp;

    //Мы вставили здесь UserResponse потому что нам не нужна полная информация юзера, а измененная.

    private UserResponse user;

    private String title;

    private String announce;

    private Integer likeCount;

    private Integer dislikeCount;

    private Integer commentCount;

    private Integer viewCount;

}
