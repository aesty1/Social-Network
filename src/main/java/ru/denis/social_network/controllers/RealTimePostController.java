package ru.denis.social_network.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate messagingTemplate;
    private final MyPostService myPostService;
    private final Map<String, Long> userLastPostIds = new ConcurrentHashMap<>();

    @MessageMapping("/posts.init")
    public void initSession(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        sendPostToUser(sessionId, null);
    }

    @MessageMapping("/posts.next")
    public void requestNextPost(@Payload Long lastPostId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        sendPostToUser(sessionId, lastPostId);
    }

    private void sendPostToUser(String sessionId, Long lastId) {
        try {
            PostDto post = myPostService.getNextPost(lastId);

            // Используем convertAndSendToUser.
            // В Spring это отправит данные в /user/queue/posts
            // (префикс /user/ добавляется автоматически)
            if (post != null) {
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/posts", post);
                userLastPostIds.put(sessionId, post.getId());
            } else {
                // Вместо строки отправляем пустой объект или спец. статус
                // Для простоты Android-кода отправим null или пустой DTO
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/posts", "EOF");
            }
        } catch (Exception e) {
            log.error("Error sending post", e);
        }
    }
}