package ru.denis.social_network.services;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyUserRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Service
public class MyCustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MyUserRepository myUserRepository;

    public MyCustomOAuth2UserService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return oAuth2User;
    }
}
