package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.denis.social_network.models.dto.UserDto;
import ru.denis.social_network.services.MyUserService;

@Controller
public class SearchController {

    @Autowired
    private MyUserService myUserService;

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @MessageMapping("/search")
    @SendTo("/topic/search")
    public void sendMessage(@Payload UserDto user, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        myUserService.getUserByName(user.getName(), sessionId);
    }

}
