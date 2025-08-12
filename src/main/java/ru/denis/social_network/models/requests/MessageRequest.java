package ru.denis.social_network.models.requests;

import lombok.Data;

@Data
public class MessageRequest {
    private int chatId;
    private int senderId;
    private int recipientId;
    private String content;
}
