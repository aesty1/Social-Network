package ru.denis.social_network.services;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyFriend;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.FriendDto;
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

    @Transactional
    @Cacheable(value = "allFriends", key = "#userId")
    public List<FriendDto> getFriends(int userId) {
        MyUser user = myUserRepository.findById(userId).orElse(null);
        List<MyFriend> friends = myFriendRepository.findByUser(user);

        List<FriendDto> friendDtos = friends.stream()
                .map(friend -> {
                    FriendDto dto = new FriendDto();
                    dto.setFriend_id(friend.getFriend().getId());
                    dto.setFriend_name(friend.getFriend().getName());
                    dto.setUser_id(friend.getUser().getId());
                    dto.setUser_name(friend.getUser().getName());
                    dto.setUser_nickname(friend.getUser().getNickname());
                    dto.setFriend_nickname(friend.getFriend().getNickname());
                    return dto;
                })
                .collect(Collectors.toList());
        return friendDtos;
    }

    @Transactional
    @CacheEvict(cacheNames = "allFriends")
    public void deleteFriend(int user_id, int friendId) {
        myFriendRepository.deleteAllByUserAndFriend(myUserRepository.getReferenceById(user_id), myUserRepository.getReferenceById(friendId));
        myFriendRepository.deleteAllByUserAndFriend(myUserRepository.getReferenceById(friendId), myUserRepository.getReferenceById(user_id));
        MyFriendRequest request =  myFriendRequestRepository.findBySenderAndReceiver(myUserRepository.getReferenceById(user_id), myUserRepository.getReferenceById(friendId));

        if(request == null) {
            request =  myFriendRequestRepository.findBySenderAndReceiver(myUserRepository.getReferenceById(friendId), myUserRepository.getReferenceById(user_id));
        }

        request.setStatus("DECLINED");
        myFriendRequestRepository.save(request);
    }
}
