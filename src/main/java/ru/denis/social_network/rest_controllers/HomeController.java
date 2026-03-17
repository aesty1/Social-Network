package ru.denis.social_network.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public ResponseEntity<?> home(Model model) {
        return ResponseEntity
                .status(HttpStatus.FOUND) // Код 302
                .header(HttpHeaders.LOCATION, "/posts")
                .build();
    }

}
