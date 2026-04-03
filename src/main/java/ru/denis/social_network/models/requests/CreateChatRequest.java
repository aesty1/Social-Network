package ru.denis.social_network.models.requests;

import lombok.Data;

@Data
public class CreateChatRequest {
    private Long user1Id;
    private Long user2Id;
}
