package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.denis.social_network.services.MyCommentService;
import ru.denis.social_network.services.MyPostService;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private MyCommentService myCommentService;

    @Autowired
    private MyPostService myPostService;

    @GetMapping()
    public String feed() {
        return "posts";
    }

    @GetMapping("/{postId}/comments")
    public String comments(@PathVariable("postId") int postId, Model model) {
        model.addAttribute("comments", myCommentService.getMyCommentsByPostId(postId));

        return "comments";
    }

    @PostMapping("/{postId}/like")
    public String like(@PathVariable("postId") int postId) {
        myPostService.incrementLikeCount(postId);

        return "redirect:/posts";
    }
}
