package ru.denis.social_network.rest_controllers.chat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.jwts.JwtProvider;
import ru.denis.social_network.models.MyChat;
import ru.denis.social_network.models.MyMessage;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.ChatDto;
import ru.denis.social_network.models.dto.MessageDTO;
import ru.denis.social_network.models.requests.CreateChatRequest;
import ru.denis.social_network.services.MyChatService;
import ru.denis.social_network.services.MyMessageService;
import ru.denis.social_network.services.MyUserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j  // ВАЖНО: добавляет логгер
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private MyChatService myChatService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private MyMessageService myMessageService;

    @PostMapping("/chat/create")
    public ResponseEntity<Void> createChat(@RequestBody @Valid CreateChatRequest request) {
        log.info(">>> CHAT_CONTROLLER_V2: createChat called");
        myChatService.createChat(request.getUser1Id(), request.getUser2Id());

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/api/chats")
                .build();
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getAllChats(Model model, HttpServletRequest request) {
        log.info(">>> CHAT_CONTROLLER_V2: getAllChats called");

        Map<String, Object> response = new HashMap<>();
        response.put("chats", myChatService.getUsersChats(getCurrentUserId(request)));
        response.put("nickname", myUserService.getUserById(getCurrentUserId(request)).getNickname());
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable @Min(1) int chatId, Model model, HttpServletRequest request) {
        log.info(">>> CHAT_CONTROLLER_V2: getChat called for chatId={}", chatId);

        ChatDto chat = myChatService.getChatDtoById(chatId);
        Long currentUserId = getCurrentUserId(request);

        log.info(">>> CHAT_CONTROLLER_V2: currentUserId={}", currentUserId);

        // Загружаем сообщения
        List<MyMessage> messages = myMessageService.getMessagesSortedByTime(chatId);
        log.info(">>> CHAT_CONTROLLER_V2: loaded {} messages from DB", messages.size());

        // Конвертируем с явным логированием
        List<MessageDTO> messageDTOs = new ArrayList<>();
        for (MyMessage msg : messages) {
            MessageDTO dto = new MessageDTO();
            dto.setChatId(chatId);

            // Явно достаём sender
            MyUser sender = msg.getSender();
            if (sender != null) {
                dto.setSenderId(sender.getId());
                dto.setSenderNickname(sender.getNickname());
                log.info(">>> CHAT_CONTROLLER_V2: msg='{}' senderId={} senderNickname={}",
                        msg.getContent(), sender.getId(), sender.getNickname());
            } else {
                dto.setSenderId(null);
                dto.setSenderNickname(null);
                log.warn(">>> CHAT_CONTROLLER_V2: msg='{}' has NULL sender!", msg.getContent());
            }

            dto.setContent(msg.getContent());
            dto.setSentAt(msg.getSentAt());
            messageDTOs.add(dto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("chat", chat);
        response.put("messages", messageDTOs);
        response.put("currentUserId", currentUserId);
        response.put("recipId", (chat.getUser1_id() == currentUserId) ? chat.getUser2_id() : chat.getUser1_id());
        response.put("currentUser", myUserService.getUserById(currentUserId));
        response.put("nickname", myUserService.getUserById(currentUserId).getNickname());

        log.info(">>> CHAT_CONTROLLER_V2: returning {} messageDTOs", messageDTOs.size());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = null;

        // 1. Пытаемся достать токен из заголовка Authorization (для Android)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Если в заголовке нет, пытаемся достать из Cookies (для браузера/Thymeleaf)
        if (token == null && request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cook -> "JWT_TOKEN".equals(cook.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        // 3. Если токена нет нигде — возвращаем -1 (ошибка авторизации)
        if (token == null) {
            return -1L;
        }

        try {
            // Используем твой jwtProvider для получения имени
            String username = jwtProvider.extractUsername(token);
            MyUser user = myUserService.getUserByUsername(username);

            return (user != null) ? user.getId() : -1L;
        } catch (Exception e) {
            // Если токен просрочен или "кривой"
            return -1L;
        }
    }
}