package ru.denis.social_network.services;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.denis.social_network.models.MyComment;
import ru.denis.social_network.models.MyPost;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.models.dto.CommentDto;
import ru.denis.social_network.models.dto.PostDto;
import ru.denis.social_network.models.dto.UserDto;
import ru.denis.social_network.repositories.MyCommentRepository;
import ru.denis.social_network.repositories.MyPostRepository;
import ru.denis.social_network.repositories.MyUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyPostService {
    @Autowired
    private MyPostRepository myPostRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private MyCommentRepository myCommentRepository;

    @Autowired
    private MyCommentService myCommentService;

    @Cacheable(value = "postById", key = "#id")
    public MyPost getPostById(Long id) {
        return myPostRepository.getById(id);
    }

    @Cacheable(value = "allPosts")
    public List<MyPost> getAllPosts() {
        return myPostRepository.findAll();
    }

    @Transactional()
    public PostDto getNextPost(Long lastPostId) {
        MyPost myPost = findNextPost(lastPostId);

        if (myPost != null) {
            return convertToDto(myPost);
        }
        return null;
    }


    private MyPost findNextPost(Long lastPostId) {
        if(lastPostId == null) {
            return myPostRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
        }

        return myPostRepository.findFirstByIdLessThanOrderByCreatedAtDesc(lastPostId).orElse(null);
    }

    private PostDto convertToDto(MyPost myPost) {
        MyUser user = myPost.getUser();
        // Конвертируем список MyComments в CommentsDto
        List<CommentDto> commentsDtos = myCommentRepository.findMyCommentsByPost(myPost)
                .stream()
                .map(this::convertCommentToDto) // предполагается наличие этого метода
                .collect(Collectors.toList());

        return new PostDto(
                myPost.getId(),
                myPost.getContent(),
                myPost.getCreatedAt(),
                myPost.getLikeCount(),
                commentsDtos,
                new UserDto(
                        user.getId(),
                        user.getName(),
                        user.getNickname(),
                        user.getBio()
                )
        );
    }


    public void incrementLikeCount(int postId) {
        myPostRepository.incrementLikeCount(postId);
    }

    private CommentDto convertCommentToDto(MyComment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}
