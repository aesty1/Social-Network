package ru.denis.social_network.rest_controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.LoginForm;
import ru.denis.social_network.services.MyUserService;

import javax.validation.Valid;

@RestController
public class AuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @ModelAttribute MyUser myUser) {
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));

        myUserService.save(myUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public String login(@Valid @ModelAttribute LoginForm loginForm, Model model, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(),
                loginForm.password()
        ));

        if(authentication.isAuthenticated()) {
            String jwt = jwtProvider.createToken(myUserService.loadUserByUsername(loginForm.username()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Cookie cookie = new Cookie("JWT_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);

            response.addCookie(cookie);

            return jwt;
        } else {
            throw new UsernameNotFoundException("Bad credentials");
        }
    }

}
