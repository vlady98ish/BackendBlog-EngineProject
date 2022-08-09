package main.service;

import lombok.AllArgsConstructor;
import main.api.response.StatisticResponse;
import main.model.Post;
import main.model.PostVote;
import main.model.User;

import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service

public class StatisticService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private PostVotesRepository postVotesRepository;

    public StatisticResponse getMyStatistics(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }
        return convertToResponse(userOptional.get());
    }

    public StatisticResponse getAllStatistics(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (!settingsService.getGlobalSettings().isStatisticsIsPublic()) {
            if (userOptional.isEmpty() || userOptional.get().getIsModerator() != 1) {
                return null;
            }
        }
        return convertToAllStatisticResponse();

    }

    private StatisticResponse convertToAllStatisticResponse() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> postList = postRepository.findAll();
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setPostsCount(postList.size());
        statisticResponse.setLikesCount(postVotesRepository.countPostVoteByValue((byte) 1));
        statisticResponse.setDislikesCount(postVotesRepository.countPostVoteByValue((byte) -1));
        int viewCount = 0;
        for (Post post : postList) {
            viewCount += post.getViewCount();
        }
        statisticResponse.setViewsCount(viewCount);
        for (Post post : postList) {
            LocalDateTime dateTime = post.getTime();
            if (dateTime.isBefore(now)) {
                now = dateTime;
            }
        }
        statisticResponse.setFirstPublication(Timestamp.valueOf(now).getTime() / 1000);
        return statisticResponse;
    }

    private StatisticResponse convertToResponse(User user) {
        List<PostVote> postVoteList;
        List<Post> postList;
        postList = user.getPosts();
        postVoteList = user.getPostVotes();
        int likeCount = 0;
        int disLikeCount = 0;
        int viewsCount = 0;
        LocalDateTime time = LocalDateTime.now();
        for (PostVote postVote : postVoteList) {
            if (postVote.getValue() == 1) {
                likeCount += 1;
            } else if (postVote.getValue() == -1) {
                disLikeCount += 1;
            }
        }
        for (Post post : postList) {
            LocalDateTime dateTime = post.getTime();
            if (dateTime.isBefore(time)) {
                time = dateTime;
            }
        }

        for (Post post : postList) {
            viewsCount += post.getViewCount();
        }
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setPostsCount(postList.size());
        statisticResponse.setLikesCount(likeCount);
        statisticResponse.setDislikesCount(disLikeCount);
        statisticResponse.setViewsCount(viewsCount);

        statisticResponse.setFirstPublication(Timestamp.valueOf(time).getTime() / 1000);
        return statisticResponse;
    }
}
