package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.denis.social_network.models.dto.LoginForm;
import ru.denis.social_network.services.MyUserService;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String loginPage(Model model, LoginForm loginForm) {
        model.addAttribute("loginForm", loginForm);
        return "login";
    }

    @GetMapping("/register-oauth")
    public String registerPage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if(principal != null) {
            model.addAttribute("user", principal.getAttributes());
        }

        return "oauthRegister";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

}
