package ru.denis.social_network.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.denis.social_network.models.MyUser;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int user_id;
    private String user_name;
    private String user_nickname;
    private int friend_id;
    private String friend_name;
    private String friend_nickname;
    private LocalDateTime createdAt = LocalDateTime.now();
}
