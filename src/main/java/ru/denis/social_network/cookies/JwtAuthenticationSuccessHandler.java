//package ru.denis.social_network.cookies;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import ru.denis.social_network.jwts.JwtProvider;
//
//import java.io.IOException;
//
//public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtProvider provider;
//
//    public JwtAuthenticationSuccessHandler(JwtProvider provider) {
//        this.provider = provider;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        String jwt = provider.createToken((UserDetails) authentication.getCredentials());
//
//        Cookie cookie = new Cookie("JWT_TOKEN", jwt);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(86400);
//
//        response.addCookie(cookie);
//        response.setStatus(HttpServletResponse.SC_OK);
//    }
//}
