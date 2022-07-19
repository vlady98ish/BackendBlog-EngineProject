package main.service;

import main.api.response.AuthResponse;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public AuthResponse getAuthInfo() {
        //Пока у нас нет авторизации возвращаем false;
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(false);
        return authResponse;
    }
}
