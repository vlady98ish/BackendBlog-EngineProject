package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.LoginResponse;

import main.model.repository.UserRepository;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;


    @Autowired
    private final UserRepository userRepository;



    /*Cтатус Авторизации
       Нам надо реальзовать AuthResponse состоящий
         из результата и списка юзеров*/
    @GetMapping("/check")
    public ResponseEntity<?> getAuth(Principal principal)
    {

        if(principal == null){
            return ResponseEntity.ok(Map.of("result", false));
        }

        return ResponseEntity.ok(authService.getLoginResponse(principal.getName()));

    }


    @GetMapping("/captcha")
    public ResponseEntity<Map<String,String>> getCaptcha()
    {
        return ResponseEntity.ok(authService.getCaptcha());
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String,Boolean>> logout(HttpServletRequest request, HttpServletResponse response){



            return ResponseEntity.ok(Map.of("result",authService.logout(request,response)));

    }



    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> authRegistr(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.authRegister(registerRequest));
    }
}
