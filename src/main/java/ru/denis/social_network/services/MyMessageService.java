package ru.denis.social_network.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public List<MyMessage> getMessagesSortedByTime(int chatId) {
        // Получаем сообщения из БД с сортировкой по возрастанию времени (ASC - от старых к новым)
        List<MyMessage> messages = myMessageRepository.findByChatOrderBySentAtAsc(myChatService.getChatById(chatId));

        // Конвертируем в DTO и возвращаем
        return messages;
    }



    @Transactional
    public void sendMessage(int chatId, int senderId, String content) {
        MyChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        MyUser sender = myUserRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MyMessage message = new MyMessage();
        message.setChat(chat);
        message.setSender(sender);
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

        messagingTemplate.convertAndSend("/topic/chat/" + chatId, dto);
    }
}
