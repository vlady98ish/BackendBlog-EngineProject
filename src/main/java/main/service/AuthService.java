package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.request.RegisterRequest;
import main.api.response.AuthResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;
import main.model.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    public AuthResponse getAuthInfo() {
        //Пока у нас нет авторизации возвращаем false;
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(false);
        return authResponse;
    }



    public Map<String,String> getCaptcha(){
        Cage cage = new GCage();
        String secret = cage.getTokenGenerator().next();
        String code =cage.getTokenGenerator().next();
        String encodedString = "";
        CaptchaCode captchaCode = new CaptchaCode();
        Map<String,String> mapCaptchaResponse = new LinkedHashMap<>();
        try (OutputStream os = new FileOutputStream("image.png", false)){
            cage.draw(code,os);

            byte[] fileContent = FileUtils.readFileToByteArray(new File("image.png"));
            encodedString = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e){
            e.printStackTrace();
        }
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secret);
        captchaCode.setTime(LocalDateTime.now());
        captchaCodeRepository.save(captchaCode);
        mapCaptchaResponse.put("secret",secret);
        mapCaptchaResponse.put("image","data:image/png;base64," + encodedString);
        clearOldCaptcha();
        return mapCaptchaResponse;
    }

    private void clearOldCaptcha(){
        List<CaptchaCode> captchaCodeList = captchaCodeRepository.findAll();
        LocalDateTime timeMinusHour = LocalDateTime.now().minusHours(1);
        captchaCodeList = captchaCodeList.stream().filter(captchaCode -> captchaCode.getTime().isBefore(timeMinusHour)).collect(Collectors.toList());
        captchaCodeRepository.deleteAll(captchaCodeList);
    }

    public Map<String,Object> authRegister(RegisterRequest registerRequest){
        boolean result = true;
        Map<String,Object> mapResponse = new LinkedHashMap<>();
        Map<String,String> errorsResponse = new LinkedHashMap<>();
        User user = userRepository.findUserByEmail(registerRequest.getEmail());
        if(user != null){
            errorsResponse.put("email", "Этот e-mail уже зарегестрирован");
            result = false;

        }
        if(registerRequest.getName().matches("[^a-zA-Z0-9]")){
            errorsResponse.put("name","Имя указано неверно");
            result = false;
        }
        if(registerRequest.getPassword().length()<6){
            errorsResponse.put("password","Пароль короче 6-ти символов");
            result = false;
        }
        Optional<CaptchaCode> captchaCode = captchaCodeRepository.getCaptchaCodeBySecretCode(registerRequest.getCaptchaSecret());
        if(captchaCode.isPresent()){
            if(!registerRequest.getCaptcha().equals(captchaCode.get().getCode())){
                errorsResponse.put("captcha", "Код с картинки введён неверно");
                result = false;
            }
        }

        if(result){
            User newUser = new User();
            newUser.setName(registerRequest.getName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setIsModerator((byte)0);
            newUser.setRegTime(LocalDateTime.now());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setCode(registerRequest.getCaptcha());
            newUser.setPhoto(null);
            //TODO: Encrypto password
            userRepository.save(newUser);
            return Map.of("result",result);
        }
        mapResponse.put("result",result);
        mapResponse.put("errors",errorsResponse);

            return mapResponse;


    }
}
