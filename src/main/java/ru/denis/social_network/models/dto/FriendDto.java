package ru.denis.social_network.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.denis.social_network.models.MyUser;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    private int id;
    private int user_id;
    private int friend_id;
    private LocalDateTime createdAt = LocalDateTime.now();
}
