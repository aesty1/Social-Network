package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.dto.MyFriendRequest;
import ru.denis.social_network.models.MyUser;

import java.util.List;

@Repository
public interface MyFriendRequestRepository extends JpaRepository<MyFriendRequest, Integer> {
    List<MyFriendRequest> findByReceiverAndStatus(MyUser receiver, String status);
    List<MyFriendRequest> findBySender(MyUser sender);
    MyFriendRequest findBySenderAndReceiver(MyUser sender, MyUser receiver);
}
