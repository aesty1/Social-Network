package ru.denis.social_network.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyMessage;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.MessageDTO;
import ru.denis.social_network.repositories.MyChatRepository;
import ru.denis.social_network.repositories.MyMessageRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyMessageService {
    private final MyMessageRepository myMessageRepository;
    private final MyChatService myChatService;
    private final MyChatRepository chatRepository;
    private final MyUserRepository myUserRepository;
    private final MyUserService myUserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpMessagingTemplate chatMessagingTemplate;

    @Cacheable(value = "messagesSortedByTime", key = "#chatId")
    public List<MyMessage> getMessagesSortedByTime(int chatId) {
        // Получаем сообщения из БД с сортировкой по возрастанию времени (ASC - от старых к новым)
        List<MyMessage> messages = myMessageRepository.findByChatOrderBySentAtAsc(myChatService.getChatById(chatId));

        // Конвертируем в DTO и возвращаем
        return messages;
    }

    public List<MyUser> getSendersByChatId(int chatId) {
        List<MyUser> users = new ArrayList<>();
        users.add(chatRepository.getReferenceById(chatId).getUser1());
        users.add(chatRepository.getReferenceById(chatId).getUser2());
        return users;
//        return chatRepository.getReferenceById(chatId).getMessages().stream().findFirst().get().getSender();
    }

    @Transactional
    @CacheEvict(value = "messagesSortedByTime", allEntries = true)
    public void sendMessage(int chatId, int senderId, int recipientId, String content) {
        MyChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        MyUser sender = myUserRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        MyUser recipient = myUserRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MyMessage message = new MyMessage();
        message.setChat(chat);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        myMessageRepository.save(message);

        // Обновляем время последнего сообщения в чате
        chat.setLastMessageAt(LocalDateTime.now());
        chatRepository.save(chat);

        // Отправляем сообщение через WebSocket
        MessageDTO dto = new MessageDTO();
        dto.setChatId(chatId);
        dto.setSenderId(senderId);
        dto.setSenderNickname(sender.getNickname());
        dto.setContent(content);
        dto.setSentAt(message.getSentAt());

        System.out.println("Sending WebSocket message to /topic/chat/" + chatId + ": " + dto);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, dto);
    }
}
