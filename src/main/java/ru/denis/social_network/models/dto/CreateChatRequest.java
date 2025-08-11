package ru.denis.social_network.models.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateChatRequest {
    private int user1Id;
    private int user2Id;
}
