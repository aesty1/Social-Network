package ru.denis.social_network.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.denis.social_network.models.MyComment;
import ru.denis.social_network.models.MyUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private List<CommentDto> comments = new ArrayList<>();
    private UserDto author;
}
