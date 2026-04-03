package ru.denis.social_network.models.requests;

import lombok.Data;

@Data
public class MessageRequest {
    private int chatId;
    private Long senderId;
    private Long recipientId;
    private String content;
}
