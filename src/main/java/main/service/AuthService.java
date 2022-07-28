package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.AllArgsConstructor;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.AuthResponse;
import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;
import main.model.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        userResponse.setId(currentUser.getId());
        userResponse.setName(currentUser.getName());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserResponse(userResponse);
        return loginResponse;
    }


    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return getLoginResponse(user.getUsername());
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return true;
    }

    public Map<String, String> getCaptcha() {
        CaptchaCode captchaCode = new CaptchaCode();
        Cage cage = new GCage();
        String secret = UUID.randomUUID().toString();
        String code = cage.getTokenGenerator().next();
        String encodedString = "";

        Map<String, String> mapCaptchaResponse = new LinkedHashMap<>();
        encodedString = Base64.getEncoder().encodeToString(cage.draw(code));
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secret);
        captchaCode.setTime(LocalDateTime.now());
        captchaCodeRepository.save(captchaCode);
        mapCaptchaResponse.put("secret", secret);
        mapCaptchaResponse.put("image", "data:image/png;base64," + encodedString);
        return mapCaptchaResponse;
    }

    /* after 1 hour run this function*/
    @Scheduled(fixedRate = 3600000)

    private void clearOldCaptcha() {
        List<CaptchaCode> captchaCodeList = captchaCodeRepository.findAll();
        captchaCodeRepository.deleteAll(captchaCodeList);

    }

    public Map<String, Object> authRegister(RegisterRequest registerRequest) {
        boolean result = true;
        Map<String, Object> mapResponse = new LinkedHashMap<>();
        Map<String, String> errorsResponse = new LinkedHashMap<>();
        Optional<User> user = userRepository.findUserByEmail(registerRequest.getEmail());
        if (user.isPresent()) {
            errorsResponse.put("email", "Этот e-mail уже зарегестрирован");
            result = false;

        }
        if (registerRequest.getName().matches("[^a-zA-Z0-9]")) {
            errorsResponse.put("name", "Имя указано неверно");
            result = false;
        }
        if (registerRequest.getPassword().length() < 6) {
            errorsResponse.put("password", "Пароль короче 6-ти символов");
            result = false;
        }
        Optional<CaptchaCode> captchaCode = captchaCodeRepository
                .getCaptchaCodeBySecretCode(registerRequest.getCaptchaSecret());
        if (captchaCode.isPresent()) {
            if (!registerRequest.getCaptcha().equals(captchaCode.get().getCode())) {
                errorsResponse.put("captcha", "Код с картинки введён неверно");
                result = false;
            }
        }

        if (result) {
            User newUser = new User();
            newUser.setName(registerRequest.getName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setIsModerator((byte) 0);
            newUser.setRegTime(LocalDateTime.now());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setCode(registerRequest.getCaptcha());
            newUser.setPhoto(null);
            //TODO: Encrypto password
            userRepository.save(newUser);
            return Map.of("result", result);
        }
        mapResponse.put("result", result);
        mapResponse.put("errors", errorsResponse);

        return mapResponse;


    }
}
