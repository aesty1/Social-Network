package ru.denis.social_network.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.denis.social_network.models.MyPost;
import ru.denis.social_network.models.MyUser;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private int id;
    private String username;
    private String text;
    private LocalDateTime createdAt;
}
