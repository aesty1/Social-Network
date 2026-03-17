package ru.denis.social_network.rest_controllers.chat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChatDto;
import ru.denis.social_network.models.requests.CreateChatRequest;
import ru.denis.social_network.services.MyChatService;
import ru.denis.social_network.services.MyMessageService;
import ru.denis.social_network.services.MyUserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private MyChatService myChatService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private MyMessageService myMessageService;

    @PostMapping("/chat/create")
    public ResponseEntity<Void> createChat(@RequestBody @Valid CreateChatRequest request) {
        myChatService.createChat(request.getUser1Id(), request.getUser2Id());

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/api/chats")
                .build();
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getAllChats(Model model, HttpServletRequest request) {
//        model.addAttribute("chats", myChatService.getUsersChats(getCurrentUserId(request)));
//        model.addAttribute("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());

        Map<String, Object> response = new HashMap<>();

        response.put("chats", myChatService.getUsersChats(getCurrentUserId(request)));
        response.put("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable @Min(1) int chatId, Model model, HttpServletRequest request) {
        ChatDto chat = myChatService.getChatDtoById(chatId);

//        model.addAttribute("chat", chat);
//        model.addAttribute("messages", myMessageService.getMessagesSortedByTime(chatId));
//        model.addAttribute("currentUserId", getCurrentUserId(request));
//        model.addAttribute("recipId", (chat.getUser1_id() == getCurrentUserId(request)) ? chat.getUser2_id() : chat.getUser1_id());
//        model.addAttribute("currentUser", myUserService.getUserById(getCurrentUserId(request)));
//        model.addAttribute("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());

        Map<String, Object> response = new HashMap<>();

        response.put("chat", chat);
        response.put("messages", myMessageService.getMessagesSortedByTime(chatId));
        response.put("currentUserId", getCurrentUserId(request));
        response.put("recipId", (chat.getUser1_id() == getCurrentUserId(request)) ? chat.getUser2_id() : chat.getUser1_id());
        response.put("currentUser", myUserService.getUserById(getCurrentUserId(request)));
        response.put("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
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
