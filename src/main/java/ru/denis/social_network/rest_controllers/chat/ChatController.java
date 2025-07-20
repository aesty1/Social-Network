package ru.denis.social_network.rest_controllers.chat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyMessage;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.CreateChatRequest;
import ru.denis.social_network.services.MyChatService;
import ru.denis.social_network.services.MyMessageService;
import ru.denis.social_network.services.MyUserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final MyChatService myChatService;
    private final JwtProvider jwtProvider;
    private final MyUserService myUserService;
    private final MyMessageService myMessageService;

    @PostMapping
    public MyChat createChat(@RequestBody CreateChatRequest createChatRequest) {
        return myChatService.createChat(createChatRequest.getParticipantIds());
    }



    @GetMapping("/chats")
    public String getAllChats(Model model, HttpServletRequest request) {
        model.addAttribute("chats", myChatService.getUsersChats(getCurrentUserId(request)));
        model.addAttribute("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());
        return "chats";
    }

    @GetMapping("/chat/{chatId}")
    public String getChat(@PathVariable int chatId, Model model, HttpServletRequest request) {
        System.out.println();
        model.addAttribute("chat", myChatService.getChatById(chatId));
        model.addAttribute("messages", myMessageService.getMessagesSortedByTime(chatId));


        model.addAttribute("currentUserId", getCurrentUserId(request));
        model.addAttribute("currentUser", myUserService.getUserById(getCurrentUserId(request)));
        model.addAttribute("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());

        return "chat";
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
