package ru.denis.social_network.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyChatParticipant;
import ru.denis.social_network.models.MyMessage;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChatDto;
import ru.denis.social_network.models.dto.UserDto;
import ru.denis.social_network.repositories.MyChatParticipantRepository;
import ru.denis.social_network.repositories.MyChatRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyChatService {

    @Autowired
    private MyChatRepository myChatRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Lazy
    @Autowired
    private MyChatParticipantRepository myChatParticipantRepository;


    public boolean existsByUser1IdAndUser2Id(int user1Id, int user2Id) {
        return myChatRepository.existsByUser1IdAndUser2Id(user1Id, user2Id);
    }

    @Caching(evict = {
            @CacheEvict(value = "usersChats", allEntries = true),
            @CacheEvict(value = "chat", allEntries = true)
    })
    public MyChat createChat(int user1Id, int user2Id) {
        if (user1Id == user2Id) {
            throw new IllegalArgumentException("Cannot create chat with yourself");
        }

        int lowerUserId = Math.min(user1Id, user2Id);
        int higherUserId = Math.max(user1Id, user2Id);

        if (myChatRepository.existsByUser1IdAndUser2Id(lowerUserId, higherUserId)) {
            throw new IllegalStateException("Chat between these users already exists");
        }

        MyUser user1 = myUserRepository.findById(lowerUserId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + lowerUserId + " not found"));
        MyUser user2 = myUserRepository.findById(higherUserId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + higherUserId + " not found"));

        MyChat chat = new MyChat();
        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setCreatedAt(LocalDateTime.now());

        MyChat savedChat = myChatRepository.save(chat);

        addParticipantsToChatInternal(savedChat.getChatId(), lowerUserId, higherUserId);

        return savedChat;
    }

    private void addParticipantsToChatInternal(int chatId, int user1Id, int user2Id) {
        MyUser user1 = myUserRepository.findMyUserById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user1Id));

        MyChatParticipant participant1 = new MyChatParticipant();
        participant1.setChat(myChatRepository.getById(chatId));
        participant1.setUser(user1);
        myChatParticipantRepository.save(participant1);

        MyUser user2 = myUserRepository.findMyUserById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user2Id));

        MyChatParticipant participant2 = new MyChatParticipant();
        participant2.setChat(myChatRepository.getById(chatId));
        participant2.setUser(user2);
        myChatParticipantRepository.save(participant2);
    }

    @Cacheable(value = "usersChats", key = "#userId")
    @Transactional()
    public List<ChatDto> getUsersChats(int userId) {
        MyUser user = myUserRepository.findMyUserById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<MyChat> chats = myChatRepository.findAllByUser1_OrUser2(user, user);

        return chats.stream()
                .map(chat -> {
                    ChatDto dto = new ChatDto();
                    dto.setChatId(chat.getChatId());
                    dto.setUser1_name(chat.getUser1().getNickname());
                    dto.setUser2_name(chat.getUser2().getNickname());
                    dto.setCreatedAt(chat.getCreatedAt());
                    dto.setLastMessageAt(chat.getLastMessageAt());
                    dto.setMessages(chat.getMessages());
                    return dto;
                })
                .collect(Collectors.toList());
    }

//    @Cacheable(value = "chatDto", key = "#chatId")
    public ChatDto getChatDtoById(int chatId) {
        MyChat chat = myChatRepository.findById(chatId).orElse(null);
        return new ChatDto(
                chat.getChatId(),
                chat.getUser1().getNickname(),
                chat.getUser2().getNickname(),
                chat.getUser1().getId(),
                chat.getUser2().getId(),
                chat.getCreatedAt(),
                chat.getLastMessageAt(),
                chat.getMessages()
        );
    }

    @Cacheable(value = "chat", key = "#chatId")
    public MyChat getChatById(int chatId) {
        return myChatRepository.findById(chatId).orElse(null);
    }
}
