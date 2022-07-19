package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GeneralController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private  InitResponse initResponse;
    @Autowired
    private  TagService tagService;


    @GetMapping("/init")
    public InitResponse init()
    {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<?> settings()
    {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public ResponseEntity<?> getTags(@RequestParam(defaultValue = "") String query)
    {
        return tagService.getTasks(query);
    }

}
