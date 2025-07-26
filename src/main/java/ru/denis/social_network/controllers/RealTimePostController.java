package ru.denis.social_network.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Initializing session: {}", sessionId);

        try {
            PostDto post = myPostService.getNextPost(null);
            log.info("Post retrieved: {}", post != null ? post.getId() : "NULL");
            messagingTemplate.convertAndSend(
                    "/topic/posts/" + sessionId,
                    post != null ? post : "NO_POSTS_AVAILABLE"
            );
        } catch (Exception e) {
            log.error("Error in initSession", e);
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/topic/errors",
                    "SERVER_ERROR: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/posts.next")
    public void requestNextPost(@Payload(required = false) Long lastPostIdStr,
                                SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        try {
            Long lastPostId = lastPostIdStr;


            log.info("Requesting next post for session: {}, lastPostId: {}", sessionId, lastPostId);
            userLastPostIds.put(sessionId, lastPostId);
            sendNextPost(sessionId);
        } catch (NumberFormatException e) {
            log.error("Invalid lastPostId format: {}", lastPostIdStr, e);
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/topic/errors",
                    "Invalid post ID format"
            );
        } catch (Exception e) {
            log.error("Error in requestNextPost", e);
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/topic/errors",
                    "SERVER_ERROR: " + e.getMessage()
            );
        }
    }

    private void sendNextPost(String sessionId) {
        Long lastPostId = userLastPostIds.get(sessionId);
        PostDto nextPost = myPostService.getNextPost(lastPostId);

        if (nextPost != null) {
            messagingTemplate.convertAndSend("/topic/posts/" + sessionId, nextPost);
            userLastPostIds.put(sessionId, nextPost.getId());
        } else {
            messagingTemplate.convertAndSend("/topic/posts/" + sessionId, "NO_POSTS_AVAILABLE");
        }
    }
}