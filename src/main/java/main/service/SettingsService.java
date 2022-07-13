package main.service;

import main.api.response.SettingsResponse;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    public SettingsResponse getGlobalSettings()
    {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(true);
        settingsResponse.setPostPreModeration(false);
        settingsResponse.setStatisticsIsPublic(true);

        return settingsResponse;
    }
}
