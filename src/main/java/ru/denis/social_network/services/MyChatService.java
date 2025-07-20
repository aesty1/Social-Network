package ru.denis.social_network.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyChatParticipant;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyChatParticipantRepository;
import ru.denis.social_network.repositories.MyChatRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyChatService {
    private final MyChatRepository myChatRepository;
    private final MyUserRepository myUserRepository;
    @Lazy
    private final MyChatParticipantRepository myChatParticipantRepository;

    public MyChat createChat(Set<Integer> participantIds) {
        MyChat chat = new MyChat();
        myChatRepository.save(chat);


        addParticipantsToChatInternal(chat.getChatId(), participantIds);
        return chat;
    }

    private void addParticipantsToChatInternal(int chatId, Set<Integer> participantIds) {
        participantIds.forEach(userId -> {
            MyUser user = myUserRepository.findMyUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            MyChatParticipant participant = new MyChatParticipant();
            participant.setChat(myChatRepository.getById(chatId));
            participant.setUser(user);

            myChatParticipantRepository.save(participant);

        });
    }

    public List<MyChat> getUsersChats(int userId) {
        return myChatRepository.findAllByUser1_OrUser2(myUserRepository.findMyUserById(userId).orElse(null), myUserRepository.findMyUserById(userId).orElse(null));
    }

    public MyChat getChatById(int chatId) {
        return myChatRepository.findById(chatId).orElse(null);
    }

}
