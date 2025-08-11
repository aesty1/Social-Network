package ru.denis.social_network.models.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.denis.social_network.models.MyChatParticipant;
import ru.denis.social_network.models.MyMessage;
import ru.denis.social_network.models.MyUser;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int chatId;
    private String user1_name;
    private String user2_name;
    private int user1_id;
    private int user2_id;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private Set<MyMessage> messages = new HashSet<>();


    //    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<MyChatParticipant> participants = new HashSet<>();
}
