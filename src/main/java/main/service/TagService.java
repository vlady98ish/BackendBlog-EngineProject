package main.service;

import main.api.response.TagResponse;
import main.model.Tag;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;


//    public ResponseEntity<?> getTasks(String query)
//    {
//        List<TagResponse> tags = new ArrayList<>();
//        List<Tag> tagList = new ArrayList<>();
//        if(query.equals(""))
//        {
//            tagList=tagRepository.findAll();
//        }
//        else
//        {
//            tagList = tagRepository.findByName(query);
//
//        }
//        for(Tag tag: tagList)
//        {
//            TagResponse tagResponse = new TagResponse();
//            tagResponse.setName();
//        }
//    }



}
