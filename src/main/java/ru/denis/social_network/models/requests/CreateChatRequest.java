package ru.denis.social_network.models.requests;

import lombok.Data;

@Data
public class CreateChatRequest {
    private int user1Id;
    private int user2Id;
}
