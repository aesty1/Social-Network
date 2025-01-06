package ru.denis.social_network.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyFriendRequest;
import ru.denis.social_network.models.MyUser;
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
}
