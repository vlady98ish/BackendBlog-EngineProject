package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.AllArgsConstructor;
import main.api.request.CodeRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;

import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;

import main.model.repository.PostRepository;
import main.model.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.time.LocalDateTime;
import java.util.*;


@Service

public class AuthService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PostRepository postRepository;

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        userResponse.setId(currentUser.getId());
        userResponse.setName(currentUser.getName());
        userResponse.setPhoto(currentUser.getPhoto());
        if (currentUser.getIsModerator() == 1) {
            userResponse.setModerationCount(postRepository.countModeratedPost());
        }
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
        if (!settingsService.getGlobalSettings().isMultiuserMode()) {
            return null;
        }
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
        if (!checkCaptcha(registerRequest.getCaptchaSecret())) {
            errorsResponse.put("captcha", "Код с картинки введён неверно");
            result = false;
        }


        if (result) {
            User newUser = new User();
            newUser.setName(registerRequest.getName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setIsModerator((byte) 0);
            newUser.setRegTime(LocalDateTime.now());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setCode(registerRequest.getCaptcha());


            userRepository.save(newUser);
            return Map.of("result", result);
        }
        mapResponse.put("result", result);
        mapResponse.put("errors", errorsResponse);

        return mapResponse;


    }

    private boolean checkCaptcha(String captcha) {
        boolean result = true;
        Optional<CaptchaCode> captchaCode = captchaCodeRepository
                .getCaptchaCodeBySecretCode(captcha);
        if (captchaCode.isPresent()) {
            if (!captcha.equals(captchaCode.get().getCode())) {

                result = false;
            }
        }

        return result;
    }

    public Map<String, Object> restore(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            return Map.of("result", false);
        }
        User user = optionalUser.get();


        String code = UUID.randomUUID().toString();


        user.setCode(code);
        userRepository.save(user);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String link = baseUrl + "/login/change-password/" + code;
        emailService.send(email, "Restore password", link);
        return Map.of("result", true);


    }


    public Map<String, Object> password(CodeRequest codeRequest) {
        boolean result = true;
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();


        if (!checkCaptcha(codeRequest.getCaptchaSecret())) {
            errors.put("captcha", "Код с картинки введён неверно");
            result = false;
        }
        if (codeRequest.getPassword().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
            result = false;
        }

        User user = userRepository.findUserByCode(codeRequest.getCode());

        if (user == null) {
            errors.put("code", "Ссылка для восстановления пароля устарела." +
                    "<a href=\"/auth/restore\"> Запросить ссылку снова");
            result = false;
        }
        response.put("result", result);
        if (!result) {
            response.put("errors", errors);
        } else {
            user.setCode(null);
            user.setPassword(passwordEncoder.encode(codeRequest.getPassword()));
        }
        return response;

    }
}
