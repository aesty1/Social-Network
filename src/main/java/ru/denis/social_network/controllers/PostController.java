package ru.denis.social_network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> comments(@PathVariable("postId") int postId, Model model) {
//        model.addAttribute("comments", myCommentService.getMyCommentsByPostId(postId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(myCommentService.getMyCommentsByPostId(postId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> like(@PathVariable("postId") int postId) {
        myPostService.incrementLikeCount((long) postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("The like was placed successfully");
    }
}
