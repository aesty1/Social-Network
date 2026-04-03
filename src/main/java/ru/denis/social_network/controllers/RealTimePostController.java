package ru.denis.social_network.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.denis.social_network.models.dto.PostDto;
import ru.denis.social_network.services.MyPostService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Controller
@RequiredArgsConstructor
public class RealTimePostController {

    private final MyPostService myPostService;

    // Инициализация первого поста
    @MessageMapping("/posts.init")
    @SendToUser("/queue/posts") // Ответ уйдет только тому, кто запросил
    public PostDto initSession() {
        log.info("Инициализация ленты");
        return myPostService.getNextPost(null);
    }

    // Запрос следующего поста
    @MessageMapping("/posts.next")
    @SendToUser("/queue/posts")
    public PostDto requestNextPost(@Payload String lastPostIdStr) {
        log.info("Запрос следующего поста после: {}", lastPostIdStr);

        // Очищаем строку от кавычек, если они прилетели из JSON
        Long lastId = null;
        if (lastPostIdStr != null && !lastPostIdStr.replace("\"", "").equals("null")) {
            lastId = Long.parseLong(lastPostIdStr.replace("\"", ""));
        }

        return myPostService.getNextPost(lastId);
    }
}