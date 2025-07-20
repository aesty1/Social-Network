package ru.denis.social_network.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChangePasswordDto;
import ru.denis.social_network.models.dto.ProfileUpdateDto;
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

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

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

    public void updateProfile(int id, ProfileUpdateDto profileUpdateDto) {
        MyUser user = myUserRepository.findMyUserById(id).orElse(null);

        if(user != null) {
            user.setNickname(profileUpdateDto.getNickname());
            user.setBio(profileUpdateDto.getBio());
            user.setName(profileUpdateDto.getName());

            myUserRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found");
        }

    }

    public MyUser getUserByUsername(String username) {
        MyUser user = myUserRepository.findMyUserByEmail(username).orElse(null);

        return user;
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

    public void changePassword(ChangePasswordDto changePasswordDto, MyUser myUser) {
        System.out.println(changePasswordDto.getOldPassword());
        System.out.println(myUser.getPassword());
        if(!passwordEncoder.matches(changePasswordDto.getOldPassword(), myUser.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        if(!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new BadCredentialsException("Password are not the same");
        }

        myUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        myUserRepository.save(myUser);
    }


    public List<MyUser> getAll() {
        return myUserRepository.findAll();
    }

    public MyUser getUserById(int id) {
        return myUserRepository.findMyUserById(id).orElse(null);
    }
}
