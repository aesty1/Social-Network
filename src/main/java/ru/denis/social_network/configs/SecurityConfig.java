package ru.denis.social_network.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.denis.social_network.jwts.JwtFilter;
import ru.denis.social_network.jwts.JwtLogoutHandler;
import ru.denis.social_network.services.MyCustomOAuth2UserService;
import ru.denis.social_network.services.MyUserService;

@Configuration
@EnableWebSecurity
@ComponentScan("ru.denis.social_network")
@EnableJpaRepositories
public class SecurityConfig {

    private final MyUserService myUserService;
    private final JwtFilter jwtFilter;
    private final MyCustomOAuth2UserService myCustomOAuth2UserService;

    public SecurityConfig(@Qualifier("myUserService") MyUserService myUserService, JwtFilter jwtFilter, MyCustomOAuth2UserService myCustomOAuth2UserService) {
        this.myUserService = myUserService;
        this.jwtFilter = jwtFilter;
        this.myCustomOAuth2UserService = myCustomOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .addLogoutHandler(new JwtLogoutHandler())
                        .logoutSuccessUrl("/login")
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register", "/login", "/authenticate/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Добавьте эту строку
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(myCustomOAuth2UserService)
                        )
                        .defaultSuccessUrl("/register-oauth", true)
                )
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public UserDetailsService userDetailsService() {
        return myUserService;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(myUserService);

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return new ProviderManager(daoAuthenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
