package ru.denis.social_network.rest_controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
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

    @GetMapping("/user")
    public ResponseEntity<?> user(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body("Hello, " + principal.getAttribute("name") + "!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @ModelAttribute MyUser myUser) {
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));

        myUserService.save(myUser);

        return ResponseEntity
                .status(HttpStatus.OK) // Код 302
                .header(HttpHeaders.LOCATION, "/login")
                .build();
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String token) {
        boolean isConfirmed = myUserService.confirmUser(token);

        if(isConfirmed) {
            return ResponseEntity.ok("Account successfully approved");
        } else {
            return ResponseEntity.badRequest().body("Error token");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@Valid @ModelAttribute LoginForm loginForm, Model model, HttpServletResponse response) {
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

            return ResponseEntity
                    .status(HttpStatus.OK) // Код 302
                    .header(HttpHeaders.LOCATION, "/me")
                    .build();
        } else {
            throw new UsernameNotFoundException("Bad credentials");
        }
    }

}
