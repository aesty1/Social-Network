package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.UserDto;
import ru.denis.social_network.services.MyUserService;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserDto> sendMessage(@Payload UserDto searchRequest) {
        // 1. Предположим, твой сервис возвращает List<MyUser>
        // Если метод называется иначе, подправь название
        List<MyUser> foundUsers = myUserService.searchUsers(searchRequest.getName());

        // 2. Маппим MyUser -> UserDto прямо здесь
        return foundUsers.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setNickname(user.getNickname());
            dto.setBio(user.getBio());
            return dto;
        }).collect(Collectors.toList());
    }

}
