package ru.denis.social_network.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantId implements Serializable {
    private int chat;
    private int user;
}