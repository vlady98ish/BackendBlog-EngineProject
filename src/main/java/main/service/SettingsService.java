package main.service;

import lombok.AllArgsConstructor;
import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService {
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {
        GlobalSettings globalSettings = globalSettingsRepository
                .findAll()
                .stream()
                .findFirst()
                .orElse(new GlobalSettings());
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(globalSettings.isMultiuserMode());
        settingsResponse.setStatisticsIsPublic(globalSettings.isStatisticsIsPublic());
        settingsResponse.setPostPreModeration(globalSettings.isPostPreModeration());

        return settingsResponse;
    }

    public void setGlobalSettings(SettingsRequest settingsRequest) {
        globalSettingsRepository.deleteAll();
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setMultiuserMode(settingsRequest.isMultiuserMode());
        globalSettings.setPostPreModeration(settingsRequest.isPostPreModeration());
        globalSettings.setStatisticsIsPublic(settingsRequest.isStatisticsIsPublic());
        globalSettingsRepository.save(globalSettings);
    }
}
