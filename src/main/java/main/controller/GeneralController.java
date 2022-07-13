package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.service.SettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;

@RestController
@RequestMapping("/api")
public class GeneralController {
    private final SettingsService settingsService;
    private final InitResponse initResponse;

    public GeneralController(InitResponse initResponse, SettingsService settingsService)
    {
        this.initResponse=initResponse;
        this.settingsService=settingsService;
    }
    @GetMapping("/init")
    public InitResponse init()
    {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings()
    {
        return settingsService.getGlobalSettings();
    }

}
