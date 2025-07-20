package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyChatRepository extends JpaRepository<MyChat, Integer> {
    List<MyChat> findAllByUser1_OrUser2(MyUser user1, MyUser user2);
//    Optional<MyChat> findById(int id);
}
