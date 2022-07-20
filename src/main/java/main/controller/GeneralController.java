package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagResponse;
import main.service.CalendarService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeneralController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private  InitResponse initResponse;
    @Autowired
    private  TagService tagService;
    @Autowired
    private CalendarService calendarService;


    @GetMapping("/init")
    public InitResponse init()
    {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings()
    {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping("/tag")
    public ResponseEntity<Map<String, List<TagResponse>>> getTags(@RequestParam(defaultValue = "") String query)
    {
        return ResponseEntity.ok(tagService.getTasks(query));
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(@RequestParam(defaultValue = "0") int year)
    {

    }

}
