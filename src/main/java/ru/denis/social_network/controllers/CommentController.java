package ru.denis.social_network.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.denis.social_network.models.MyComment;
import ru.denis.social_network.models.dto.CommentDto;
import ru.denis.social_network.services.MyCommentService;
import ru.denis.social_network.services.MyPostService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private MyCommentService myCommentService;

    @Autowired
    private MyPostService myPostService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<MyComment> comments = myCommentService.getMyCommentsByPostId(postId.intValue());

        List<CommentDto> commentDtos = comments.stream().map(comment -> {
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setUsername(comment.getUser().getNickname());
            dto.setText(comment.getText());
            dto.setCreatedAt(comment.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }
}