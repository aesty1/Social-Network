package ru.denis.social_network.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.services.MyUserService;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MyUserService myUserService;

    @GetMapping()
    public String home(Model model) {

        return "home";
    }

}
