package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.Map;

/* Response for GET /api/post */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountPostsResponse {

    private Integer count;

    private List<PostResponse> posts;
}
