package ru.denis.social_network.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.*;
import ru.denis.social_network.models.requests.CreateChatRequest;
import ru.denis.social_network.models.requests.MyFriendRequest;
import ru.denis.social_network.repositories.MyChatRepository;
import ru.denis.social_network.services.MyFriendRequestService;
import ru.denis.social_network.services.MyFriendService;
import ru.denis.social_network.services.MyUserService;

import javax.validation.constraints.Min;
import java.util.*;

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

    @Autowired
    private MyChatRepository myChatRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));
        List<FriendDto> friends = myFriendService.getFriends(user.getId());

//        model.addAttribute("user", user);
//        model.addAttribute("friends", friends);
//        model.addAttribute("users", myUserService.getAll().stream().filter(u -> u.getId() != user.getId()).collect(toList()));

        List<MyFriendRequest> filteredFriends = myFriendRequestService.findByReceiverAndStatus(user, "PENDING").stream()
                .filter(f -> f.getSender().getId() != user.getId()).toList();


        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("friends", friends);
        response.put("users", myUserService.getAll().stream().filter(u -> u.getId() != user.getId()).collect(toList()));
        if(!filteredFriends.isEmpty()) {
            response.put("filteredFriends", filteredFriends);
        } else {
            response.put("filteredFriends", new ArrayList<>());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/user/{nickname}")
    public ResponseEntity<?> getProfile(@PathVariable("nickname") String nickname, Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserByNickname(nickname);
        MyUser me = myUserService.getUserById(getCurrentUserId(request));

        if(user.getId() == me.getId()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND) // Код 302
                    .header(HttpHeaders.LOCATION, "/me")
                    .build();
        }

        List<FriendDto> friends = myFriendService.getFriends(user.getId());
//        model.addAttribute("user", user);
//        model.addAttribute("me", me);
//        model.addAttribute("friends", friends);
//        model.addAttribute("createChatRequest", new CreateChatRequest());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("me", me);
        response.put("friends", friends);
        response.put("createChatRequest", new CreateChatRequest());

        if(!myChatRepository.existsByUser1IdAndUser2Id(me.getId(), user.getId()) && !myChatRepository.existsByUser1IdAndUser2Id(user.getId(), me.getId())) {
            response.put("existsChatByUsers", true);
        } else {
            response.put("existsChatByUsers", false);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));
        List<FriendDto> friends = myFriendService.getFriends(user.getId());

//        model.addAttribute("friends", friends);

        return ResponseEntity.status(HttpStatus.OK)
                .body(friends);
    }

    @PostMapping("/friends/add/{id}")
    public ResponseEntity<?> addFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.sendFriendRequest(getCurrentUserId(request), id);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Friend add ok");
    }

    @PostMapping("/friends/remove/{id}")
    public ResponseEntity<?> removeFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendService.deleteFriend(getCurrentUserId(request), id);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Remove friend ok");
    }

    @PostMapping("/friends/respond/{id}")
    public ResponseEntity<?> respondToFriend(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.respondToFriendRequest(id, true);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Respond to friend ok");
    }

    @PostMapping("/friends/cancel/{id}")
    public ResponseEntity<?> respondToFriendCancel(@PathVariable @Min(1) int id, HttpServletRequest request) {
        myFriendRequestService.respondToFriendRequest(id, false);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Respond to friend cancel ok");
    }

    @GetMapping("/profile/edit")
    public ResponseEntity<?> showEditProfileForm(Model model, HttpServletRequest request) {
        MyUser user = myUserService.getUserById(getCurrentUserId(request));

//        model.addAttribute("user", user);
//        model.addAttribute("profileUpdateDto", new ProfileUpdateDto(user.getNickname(), user.getName(), user.getBio()));

        Map<String, Object> response = new HashMap<>();

        response.put("user", user);
        response.put("profileUpdateDto", new ProfileUpdateDto(user.getNickname(), user.getName(), user.getBio()));

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/profile/edit/save")
    public ResponseEntity<?> saveProfile(@RequestBody ProfileUpdateDto profileUpdateDto, BindingResult result, HttpServletRequest request) {
        if(result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Profile edit request has errors");
        }

        try {
            myUserService.updateProfile(getCurrentUserId(request), profileUpdateDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Profile saved");
    }

    @GetMapping("/profile/edit/password")
    public ResponseEntity<?> showEditPasswordForm(Model model) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
//        model.addAttribute("changePasswordDto", changePasswordDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(changePasswordDto);
    }

    @PostMapping("/profile/edit/password/save")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        myUserService.changePassword(changePasswordDto, myUserService.getUserById(getCurrentUserId(request)));

        return ResponseEntity.status(HttpStatus.OK)
                .body("Password successfully changed");
    }

    private int getCurrentUserId(HttpServletRequest request) {
        String token = null;

        // 1. Пытаемся достать токен из заголовка Authorization (для Android)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Если в заголовке нет, пытаемся достать из Cookies (для браузера/Thymeleaf)
        if (token == null && request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cook -> "JWT_TOKEN".equals(cook.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        // 3. Если токена нет нигде — возвращаем -1 (ошибка авторизации)
        if (token == null) {
            return -1;
        }

        try {
            // Используем твой jwtProvider для получения имени
            String username = jwtProvider.extractUsername(token);
            MyUser user = myUserService.getUserByUsername(username);

            return (user != null) ? user.getId() : -1;
        } catch (Exception e) {
            // Если токен просрочен или "кривой"
            return -1;
        }
    }
}