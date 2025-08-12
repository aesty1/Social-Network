package ru.denis.social_network.rest_controllers.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import ru.denis.social_network.services.MyFriendRequestService;

import javax.validation.Valid;

@Controller
@RequestMapping("/friends")
public class FriendRequestController {

    @Autowired
    private MyFriendRequestService myFriendRequestService;

    @PostMapping("/send")
    public RedirectView send(@Valid @RequestParam int sender_id, @Valid @RequestParam int receiver_id) {
        myFriendRequestService.sendFriendRequest(sender_id, receiver_id);

        return new RedirectView("/me");
    }

    @PostMapping("/respond")
    public RedirectView respond(@Valid @RequestParam boolean accept, @Valid @RequestParam int request_id) {
        myFriendRequestService.respondToFriendRequest(request_id, accept);

        return new RedirectView("/me");
    }
}
