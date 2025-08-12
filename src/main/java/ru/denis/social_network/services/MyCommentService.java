package ru.denis.social_network.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyComment;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.CommentDto;
import ru.denis.social_network.repositories.MyCommentRepository;
import ru.denis.social_network.repositories.MyPostRepository;

import java.util.List;

@Service
public class MyCommentService {
    
    @Autowired
    private MyCommentRepository myCommentRepository;

    @Autowired
    private MyPostRepository myPostRepository;

    @Cacheable(value = "commentsByPostId", key = "#postId")
    public List<MyComment> getMyCommentsByPostId(int postId) {
        return myCommentRepository.findMyCommentsByPost(myPostRepository.getById(Long.valueOf(postId)));
    }

    @Cacheable(value = "comment", key = "#id")
    public MyComment getCommentById(int id) {
        return myCommentRepository.getById(id);
    }

    @Cacheable(value = "allComments")
    public List<MyComment> getAllComments() {
        return myCommentRepository.findAll();
    }
}
