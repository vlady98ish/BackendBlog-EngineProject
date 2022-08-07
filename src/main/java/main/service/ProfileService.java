package main.service;


import main.model.User;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> postMyProfile(MultipartFile photo, String email, String name, String password, int removePhoto, String userEmail) throws IOException {
        boolean result = true;
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();
        Optional<User> optionalUser = userRepository.findUserByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();

        if (photo != null) {
            int MAX_SIZE = 5_000_000;
            if (photo.getBytes().length <= MAX_SIZE) {
                if (removePhoto == 1) {
                    user.setPhoto("");
                } else {
                    File image = imageService.saveImage(photo, true);
                    String photoDestination = StringUtils.cleanPath(image.getPath());
                    user.setPhoto("/" + photoDestination);
                }
            } else {
                result = false;
                errors.put("photo", "Фото слишком большое, нужно не более 5 Мб.");
            }
        }
        if (email != null && !user.getEmail().equals(email)) {
            if (changeEmail(email)) {
                user.setEmail(email);
            } else {
                errors.put("email", "Этот e-mail уже зарегистрирован");
            }
        }


        if (name != null) {
            if (!changeName(name)) {
                user.setName(name);
            } else {
                result = false;
                errors.put("name", "Имя указано неверно");
            }
        }

        if (password != null) {
            if (changePassword(password)) {
                user.setPassword(passwordEncoder.encode(password));
            } else {
                result = false;
                errors.put("password", "Пароль короче 6-ти символов");
            }
        }

        userRepository.save(user);
        response.put("result", result);
        if (!result) {
            response.put("errors", errors);
        }
        return response;

    }

    private boolean changeEmail(String email) {
        Optional<User> optionalUser;
        if (email != null) {
            optionalUser = userRepository.findUserByEmail(email);
            return optionalUser.isEmpty();

        }
        return true;
    }

    private boolean changeName(String name) {
        return !name.matches("[a-zA-Z]*") && name.length() <= 100 && name.length() >= 2;
    }

    private boolean changePassword(String password) {
        return !(password.length() < 6);
    }

}
