package main.controller;

import main.api.request.RegisterRequest;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;



    /*Cтатус Авторизации
       Нам надо реальзовать AuthResponse состоящий
         из результата и списка юзеров*/
    @GetMapping("/check")
    public ResponseEntity<?> getAuth()
    {
        return  null;
    }


    @GetMapping("/captcha")
    public ResponseEntity<Map<String,String>> getCaptcha()
    {
        return ResponseEntity.ok(authService.getCaptcha());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> authRegistr(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.authRegister(registerRequest));
    }
}
