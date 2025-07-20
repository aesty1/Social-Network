package ru.denis.social_network.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChangePasswordDto;
import ru.denis.social_network.models.dto.MyFriendRequest;
import ru.denis.social_network.models.dto.ProfileUpdateDto;
import ru.denis.social_network.services.MyFriendRequestService;
import ru.denis.social_network.services.MyFriendService;
import ru.denis.social_network.services.MyUserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Controller
public class UserController {

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private MyFriendService myFriendService;

    @Autowired
    private MyFriendRequestService myFriendRequestService;
    @Autowired
    private JwtProvider jwtProvider;
    private SimpUserRegistry userRegistry;

    @GetMapping("/me")
    public String getProfile(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));
        List<MyUser> friends = myFriendService.getFriends(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("users", myUserService.getAll().stream().filter(u -> u.getId() != user.getId()).collect(toList()));
        model.addAttribute("friend_requests", myFriendRequestService.findByReceiverAndStatus(user, "PENDING", "DECLINED"));


        // Фильтруем только DECLINED и PENDING
        List<MyFriendRequest> filteredFriends = myFriendRequestService.findByReceiverAndStatus(user, "PENDING", "DECLINED").stream()
                .filter(f -> f.getSender().getId() != user.getId()).toList();

        model.addAttribute("filteredFriends", filteredFriends);

        return "users/getOne";
    }

    @GetMapping("/friends")
    public String getFriends(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));

        model.addAttribute("friends", user.getFriends());

        return "users/friends";
    }

    @PostMapping("/friends/add/{id}")
    public ResponseEntity<?> addFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.sendFriendRequest(getCurrentUserId(request), id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/friends/remove/{id}")
    public ResponseEntity<?> remvoeFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendService.deleteFriend(getCurrentUserId(request), id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/friends/respond/{id}")
    public ResponseEntity<?> respondToFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.respondToFriendRequest(id, true);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/friends/cancel/{id}")
    public ResponseEntity<?> respondToFriendCancel(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.respondToFriendRequest(id, false);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));

        model.addAttribute("user", user);
        model.addAttribute("profileUpdateDto", new ProfileUpdateDto(user.getNickname(), user.getName(), user.getBio()));

        return "editProfile";
    }

    @PostMapping("/profile/edit/save")
    public String saveProfile(@ModelAttribute("profileUpdateDto") ProfileUpdateDto profileUpdateDto, BindingResult result, HttpServletRequest request) {
        if(result.hasErrors()) {
            return "editProfile";
        }

        try {


            myUserService.updateProfile(getCurrentUserId(request), profileUpdateDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/me";
    }

    @GetMapping("/profile/edit/password")
    public String showEditPasswordForm(Model model) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        model.addAttribute("changePasswordDto", changePasswordDto);

        return "editPassword";
    }

    @PostMapping("/profile/edit/password/save")
    public ResponseEntity<?> changePassword(@ModelAttribute ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        System.out.println(changePasswordDto.getNewPassword());
        myUserService.changePassword(changePasswordDto, myUserService.getUserById(getCurrentUserId(request)));

        return ResponseEntity.ok().build();
    }










    private int getCurrentUserId(HttpServletRequest request) {
        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(cook -> "JWT_TOKEN".equals(cook.getName()))
                .findFirst();

        if(cookie == null || !cookie.isPresent()) {
            return -1;
        }
        String username =  jwtProvider.extractUsername(cookie.get().getValue());

        MyUser user =  myUserService.getUserByUsername(username);

        return user.getId();

    }
}
