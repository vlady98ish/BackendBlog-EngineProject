package main.service;

import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {
    @Autowired
    private PostRepository postRepository;


    public CalendarResponse getCalendar(int year)
    {
        LocalDateTime now = LocalDateTime.now();
        int tempYear;
        if(year == 0)
        {
            tempYear = LocalDateTime.now().getYear();
        }
        else{
            tempYear = year;
        }
        List<Post> postList = postRepository.getActivePosts(now);

    }

    private List<Post> getPostByYear(List<Post> postList, int year)
    {
        List<Post> postsListByYear = new ArrayList<>();
        for(Post post: postList)
        {
            if(post.getTime().getYear() == year) postsListByYear.add(post);
        }
        return postsListByYear;
    }
}
