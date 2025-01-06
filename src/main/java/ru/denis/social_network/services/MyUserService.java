package ru.denis.social_network.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyFriendRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private MyFriendRepository myFriendRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser user = myUserRepository.findMyUserByEmail(email).orElse(null);

        if(user != null) {
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(email);
        }

    }

    public void save(MyUser myUser) {
        myUserRepository.save(myUser);
    }

    public MyUser getUserByNickname(String nickname) {
        MyUser user = myUserRepository.findMyUserByNickname(nickname).orElse(null);

        if(user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException(nickname);
        }
    }

    public List<MyUser> getAll() {
        return myUserRepository.findAll();
    }

}
