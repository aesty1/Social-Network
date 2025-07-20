package ru.denis.social_network.rest_controllers.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.denis.social_network.models.dto.MyFriendRequest;
import ru.denis.social_network.services.MyFriendRequestService;

@RestController
@RequestMapping("/friends")
public class FriendRequestController {

    @Autowired
    private MyFriendRequestService myFriendRequestService;


    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestParam int sender_id, @RequestParam int receiver_id) {
        MyFriendRequest request = myFriendRequestService.sendFriendRequest(sender_id, receiver_id);

        return ResponseEntity.ok("Response sended");
    }

    @PostMapping("/respond")
    public ResponseEntity<?> respond(@RequestParam boolean accept, @RequestParam int request_id) {
        myFriendRequestService.respondToFriendRequest(request_id, accept);

        return ResponseEntity.ok("Response recorded");
    }
}
