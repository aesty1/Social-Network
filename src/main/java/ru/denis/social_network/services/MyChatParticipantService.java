package ru.denis.social_network.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyChatParticipant;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyChatParticipantRepository;
import ru.denis.social_network.repositories.MyChatRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyChatParticipantService {
    @Lazy
    private final MyChatParticipantRepository chatParticipantRepository;
    private final MyChatRepository chatRepository;
    @Lazy
    private final MyChatService myChatService;
    private final MyUserService myUserService;

    // Добавление участника в чат
    @Transactional
    public MyChatParticipant addParticipantToChat(MyChat chat, MyUser user) {
        MyChatParticipant participant = MyChatParticipant.builder()
                .chat(chat)
                .user(user)
                .build();
        return chatParticipantRepository.save(participant);
    }

    @Transactional
    public HashSet<MyChatParticipant> addParticipantsToChat(int chatId, HashSet<Integer> userIds) {
        HashSet<MyChatParticipant> addedParticipants = new HashSet<>();

        // Получаем чат один раз
        MyChat chat = myChatService.getChatById(chatId);

        for (int userId : userIds) {
            // Проверяем, не является ли пользователь уже участником
            if (!chatParticipantRepository.existsByChatAndUserId(myChatService.getChatById(chatId), userId)) {
                MyUser user = myUserService.getUserById(userId);

                MyChatParticipant participant = MyChatParticipant.builder()
                        .chat(chat)
                        .user(user)
                        .build();

                addedParticipants.add(chatParticipantRepository.save(participant));
            }
        }

        return addedParticipants;
    }



    // Проверка является ли пользователь участником чата
    public boolean isUserInChat(int chatId, int userId) {
        return chatParticipantRepository.existsByChatAndUserId(myChatService.getChatById(chatId), userId);
    }

    // Получение всех участников чата
    public List<MyChatParticipant> getChatParticipants(int chatId) {
        return chatParticipantRepository.findAllByChat(chatRepository.getOne(chatId));
    }




}