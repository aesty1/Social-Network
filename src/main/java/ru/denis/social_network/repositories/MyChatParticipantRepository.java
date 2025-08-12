package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denis.social_network.models.ChatParticipantId;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyChatParticipant;

import java.util.List;

public interface MyChatParticipantRepository extends JpaRepository<MyChatParticipant, ChatParticipantId> {
    boolean existsByChatAndUserId(MyChat chat, int userId);
    List<MyChatParticipant> findAllByChat(MyChat chat);
}