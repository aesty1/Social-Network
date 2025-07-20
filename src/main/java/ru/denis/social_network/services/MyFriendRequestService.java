package ru.denis.social_network.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.dto.MyFriendRequest;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyFriendRepository;
import ru.denis.social_network.repositories.MyFriendRequestRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.util.List;

@Service
public class MyFriendRequestService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private MyFriendRequestRepository myFriendRequestRepository;

    @Autowired
    private MyFriendRepository myFriendRepository;

    public MyFriendRequest sendFriendRequest(int sender_id, int receiver_id) {
        MyUser sender = myUserRepository.findById(sender_id).orElse(null);
        MyUser receiver = myUserRepository.findById(receiver_id).orElse(null);

        if (myFriendRequestRepository.findByReceiverAndStatus(receiver, "PENDING").stream()
                .anyMatch(request -> request.getSender().equals(sender))) {
            throw new RuntimeException("Friend request already sent");
        }

        MyFriendRequest request = new MyFriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");

        return myFriendRequestRepository.save(request);

    }

    public void respondToFriendRequest(int request_id, boolean accept) {
        MyFriendRequest request = myFriendRequestRepository.findById(request_id).orElse(null);

        if (accept) {
            // Add friends in both directions
            MyFriend friendship1 = new MyFriend();
            friendship1.setUser(request.getSender());
            friendship1.setFriend(request.getReceiver());
            myFriendRepository.save(friendship1);

            MyFriend friendship2 = new MyFriend();
            friendship2.setUser(request.getReceiver());
            friendship2.setFriend(request.getSender());
            myFriendRepository.save(friendship2);

            request.setStatus("ACCEPTED");
        } else {
            request.setStatus("DECLINED");
        }

        myFriendRequestRepository.save(request);
    }

    public List<MyFriendRequest> findByReceiverAndStatus(MyUser receiver, String status, String status2) {
        return myFriendRequestRepository.findByReceiverAndStatusOrStatus(receiver, status, status2);
    }
}
