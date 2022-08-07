package main.api.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticResponse {

    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
