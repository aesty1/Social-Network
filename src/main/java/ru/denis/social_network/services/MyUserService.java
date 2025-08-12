package ru.denis.social_network.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChangePasswordDto;
import ru.denis.social_network.models.dto.ChatDto;
import ru.denis.social_network.models.dto.ProfileUpdateDto;
import ru.denis.social_network.models.dto.UserDto;
import ru.denis.social_network.repositories.MyFriendRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void getUserByName(String name, String search_id) {
        List<MyUser> users = myUserRepository.findByNameContainingIgnoreCase(name);
        List<UserDto> usersDto = new ArrayList<>();
        if(users != null) {
            usersDto = users.stream()
                    .map(user -> {
                        UserDto dto = new UserDto();
                        dto.setId(user.getId());
                        dto.setName(user.getName());
                        dto.setBio(user.getBio());
                        dto.setNickname(user.getNickname());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        if(usersDto.size() > 0) {
            messagingTemplate.convertAndSend("/topic/search/", usersDto);
        } else {
            messagingTemplate.convertAndSend("/topic/search/", new UserDto());
        }



    }

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

    @Caching(evict = {
            @CacheEvict(value = "allUsers", allEntries = true),
            @CacheEvict(value = "userById", allEntries = true)
    })
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

    @Cacheable(value = "userByUsername", key = "#username")
    public MyUser getUserByUsername(String username) {
        MyUser user = myUserRepository.findMyUserByEmail(username).orElse(null);

        return user;
    }

    public void save(MyUser myUser) {
        myUserRepository.save(myUser);
    }

    @Cacheable(value = "userByNickname", key = "#nickname")
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


    @Cacheable(value = "allUsers")
    public List<MyUser> getAll() {
        return myUserRepository.findAll();
    }

    @Cacheable(value = "userById", key = "#id")
    @Transactional
    public MyUser getUserById(int id) {
        return myUserRepository.findMyUserById(id).orElse(null);
    }
}
