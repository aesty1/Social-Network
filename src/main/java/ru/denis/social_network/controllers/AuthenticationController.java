package ru.denis.social_network.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.denis.social_network.models.dto.LoginForm;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public ResponseEntity<?> loginPage(Model model, LoginForm loginForm) {
//        model.addAttribute("loginForm", loginForm);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginForm);
    }

    @GetMapping("/register-oauth")
    public ResponseEntity<String> registerPage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if(principal != null) {
//            model.addAttribute("user", principal.getAttributes());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(principal.getAttributes().toString());
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("OAuth error: forbidden");
    }


}
