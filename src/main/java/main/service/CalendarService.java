package main.service;

import lombok.AllArgsConstructor;
import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class CalendarService {
    @Autowired
    private PostRepository postRepository;


    public CalendarResponse getCalendar(int year) {
        LocalDateTime now = LocalDateTime.now();
        int tempYear;
        if (year == 0) {
            tempYear = LocalDateTime.now().getYear();
        } else {
            tempYear = year;
        }
        List<Post> postList = postRepository.getActivePosts(now);


        return convertToCalendarResponse(postList, tempYear);

    }

    private CalendarResponse convertToCalendarResponse(List<Post> postList, int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Integer> years;
        Map<String, Long> mapOfPostDates = getMapOfPostDatesCount(postList, year);
        years = getListOfYears(postList);
        calendarResponse.setPosts(mapOfPostDates);
        calendarResponse.setYears(years);
        return calendarResponse;
    }

    private List<Integer> getListOfYears(List<Post> postList) {
        List<Integer> yearsList;
        yearsList = postList.stream().map(post -> post
                        .getTime()
                        .getYear())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        return yearsList;
    }

    private Map<String, Long> getMapOfPostDatesCount(List<Post> postList, int year) {
        List<Post> specificYearPostList = postList
                .stream()
                .filter(p -> p.getTime().getYear() == year)
                .collect(Collectors.toList());
        return specificYearPostList
                .stream()
                .collect(Collectors.groupingBy(p -> p.getTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Collectors.counting()));
    }


}
