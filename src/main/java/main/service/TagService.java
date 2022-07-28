package main.service;

import main.api.response.TagResponse;
import main.model.Post;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;


    public Map<String, List<TagResponse>> getTasks(String query) {
        LocalDateTime now = LocalDateTime.now();


        List<Tag> tagList;
        int countOfActivePost = postRepository.getCountOfActivePost(now);

        if (query.equals("")) {
            tagList = tagRepository.findAll();
        } else {
            tagList = tagRepository.findByName(query);

        }

        return convertToMapTagResponse(tagList, countOfActivePost);
    }

    //TODO:MAPPER
    private Map<String, List<TagResponse>> convertToMapTagResponse(List<Tag> tagList, int countActivePosts) {
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

    //TODO:MAPPER
    private double calculateWeight(int countPostWithTag, int countAllPost, double coefficient) {
        double dWeightTag = (double) countPostWithTag / countAllPost;


        return dWeightTag * coefficient;
    }

    //TODO:MAPPER
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


