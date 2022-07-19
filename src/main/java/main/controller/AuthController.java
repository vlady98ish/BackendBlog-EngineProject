package main.controller;

import main.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService)
    {
        this.authService=authService;
    }


    /*Cтатус Авторизации
       Нам надо реальзовать AuthResponse состоящий
         из результата и списка юзеров*/
    @GetMapping("/check")
    public ResponseEntity<?> getAuth()
    {
        return  null;
    }
}
