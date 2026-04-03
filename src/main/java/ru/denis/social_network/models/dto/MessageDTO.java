package ru.denis.social_network.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private int id;
    private int chatId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private LocalDateTime sentAt;
}