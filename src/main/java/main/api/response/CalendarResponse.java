package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {
    private Map<String, List<Integer>> allYears;

    private Map<String, Map<String,Integer>> posts;

}
