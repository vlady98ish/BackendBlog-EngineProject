package main.service;

import lombok.AllArgsConstructor;
import main.api.response.TagResponse;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service

public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MapperService mapperService;


    public Map<String, List<TagResponse>> getTasks(String query) {
        LocalDateTime now = LocalDateTime.now();


        List<Tag> tagList;
        int countOfActivePost = postRepository.getCountOfActivePost(now);

        if (query.equals("")) {
            tagList = tagRepository.findAll();
        } else {
            tagList = tagRepository.findAllByName(query);

        }

        return mapperService.convertToMapTagResponse(tagList, countOfActivePost);
    }







}


