package ru.denis.social_network.models.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateChatRequest {
    private Set<Integer> participantIds;
    private String name;
}
