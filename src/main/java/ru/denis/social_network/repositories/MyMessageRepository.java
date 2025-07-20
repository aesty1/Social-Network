package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyMessage;

import java.util.List;

@Repository
public interface MyMessageRepository extends JpaRepository<MyMessage, Integer> {
    List<MyMessage> findByChatOrderBySentAtAsc(MyChat chat);
}
