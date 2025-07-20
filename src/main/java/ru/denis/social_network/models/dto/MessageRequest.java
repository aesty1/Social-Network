package ru.denis.social_network.models.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private int chatId;
    private int senderId;
    private String content;
}
