package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.services.MyFriendRequestService;
import ru.denis.social_network.services.MyFriendService;
import ru.denis.social_network.services.MyUserService;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private MyFriendService myFriendService;

    @Autowired
    private MyFriendRequestService myFriendRequestService;

    @GetMapping("/users/{name}")
    public String getProfile(@PathVariable String name, Model model) {
        MyUser user = myUserService.getUserByNickname(name);
        List<MyUser> friends = myFriendService.getFriends(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("users", myUserService.getAll());
        model.addAttribute("friend_requests", myFriendRequestService.findByReceiverAndStatus(user, "PENDING"));

        return "users/getOne";
    }
}
