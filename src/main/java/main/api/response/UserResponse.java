package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/*User Response for CountPostsResponse*/
public class UserResponse {

    private Integer id;

    private String name;

    private String photo;
}
