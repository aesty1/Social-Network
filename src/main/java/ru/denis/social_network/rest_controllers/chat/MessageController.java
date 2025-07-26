package ru.denis.social_network.rest_controllers.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.denis.social_network.models.dto.MessageRequest;
import ru.denis.social_network.services.MyMessageService;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MyMessageService myMessageService;

    @MessageMapping("/send")
    public void sendMessage(@Payload MessageRequest request) {
        myMessageService.sendMessage(
                request.getChatId(),
                request.getSenderId(),
                request.getContent()
        );
    }
}
