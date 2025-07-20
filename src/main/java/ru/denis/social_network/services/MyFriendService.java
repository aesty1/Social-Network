package ru.denis.social_network.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.MyFriendRequest;
import ru.denis.social_network.repositories.MyFriendRepository;
import ru.denis.social_network.repositories.MyFriendRequestRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyFriendService {

    @Autowired
    private MyFriendRequestRepository myFriendRequestRepository;

    @Autowired
    private MyFriendRepository myFriendRepository;

    @Autowired
    private MyUserRepository myUserRepository;


    public List<MyUser> getFriends(int userId) {
        MyUser user = myUserRepository.findById(userId).orElse(null);

        List<MyFriend> friends = myFriendRepository.findByUser(user);

        return friends.stream().map(MyFriend::getFriend).collect(Collectors.toList());
    }

    @Transactional
    public void deleteFriend(int user_id, int friendId) {
        myFriendRepository.deleteAllByUserAndFriend(myUserRepository.getById(user_id), myUserRepository.getById(friendId));
        myFriendRepository.deleteAllByUserAndFriend(myUserRepository.getById(friendId), myUserRepository.getById(user_id));
        MyFriendRequest request =  myFriendRequestRepository.findBySenderAndReceiver(myUserRepository.getById(user_id), myUserRepository.getById(friendId));

        if(request == null) {
            request =  myFriendRequestRepository.findBySenderAndReceiver(myUserRepository.getById(friendId), myUserRepository.getById(user_id));
        }

        request.setStatus("DECLINED");
        myFriendRequestRepository.save(request);
    }
}
