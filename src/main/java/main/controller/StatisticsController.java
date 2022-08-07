package main.controller;

import lombok.AllArgsConstructor;
import main.api.response.StatisticResponse;
import main.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
public class StatisticsController {


    @Autowired
    private StatisticService statisticService;

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> getMyStatistics(Principal principal) {
        return ResponseEntity.ok(statisticService.getMyStatistics(principal.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<StatisticResponse> getAllStatistics(Principal principal) {
        StatisticResponse statisticResponse = statisticService.getAllStatistics(principal.getName());
        if (statisticResponse == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(statisticResponse);
    }
}
