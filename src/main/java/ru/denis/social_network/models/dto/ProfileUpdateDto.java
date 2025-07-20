package ru.denis.social_network.models.dto;

import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String nickname;
    private String name;
    private String bio;

    public ProfileUpdateDto(String nickname, String name, String bio) {
        this.nickname = nickname;
        this.name = name;
        this.bio = bio;
    }
}
