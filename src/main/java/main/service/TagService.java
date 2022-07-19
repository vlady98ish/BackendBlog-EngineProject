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


    public ResponseEntity<?> getTasks(String query) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, List<TagResponse>> tagsMapResponse = new LinkedHashMap<>();
        List<TagResponse> tags = new ArrayList<>();
        List<Tag> tagList;
        int countOfActivePost = postRepository.getCountOfActivePost(now);

        if (query.equals("")) {
            tagList = tagRepository.findAll();
        } else {
            tagList = tagRepository.findByName(query);

        }
        double k = getCoefficient(tagList, countOfActivePost);
        for (Tag tag : tagList) {
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(tag.getName());
            int countPostWithTag = tag.getPostList().size();
            tagResponse.setWeight(calculateWeight(countPostWithTag, countOfActivePost, k));
            tags.add(tagResponse);

        }

        tagsMapResponse.put("tags", tags);
        return new ResponseEntity<>(tagsMapResponse, HttpStatus.OK);
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


