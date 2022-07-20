package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {
        GlobalSettings globalSettings = globalSettingsRepository.findAll().stream().findFirst().orElse(new GlobalSettings());
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(globalSettings.isMultiuserMode());
        settingsResponse.setStatisticsIsPublic(globalSettings.isStatisticsIsPublic());
        settingsResponse.setPostPreModeration(globalSettings.isPostPreModeration());

        return settingsResponse;
    }
}
