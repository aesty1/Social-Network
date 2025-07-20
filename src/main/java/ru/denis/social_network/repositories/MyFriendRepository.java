package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;

import java.util.List;

@Repository
public interface MyFriendRepository extends JpaRepository<MyFriend, Integer> {
    List<MyFriend> findByUser(MyUser user);
    List<MyFriend> findByFriend(MyUser friend);
    void deleteAllByUserAndFriend(MyUser user, MyUser friend);
}
