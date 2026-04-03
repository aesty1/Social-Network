package ru.denis.social_network.services;

import jakarta.transaction.Transactional;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyPostService {

    @Autowired
    private MyPostRepository myPostRepository;

    @Autowired
    private MyCommentRepository myCommentRepository;

    @Cacheable(value = "postById", key = "#id")
    public MyPost getPostById(Long id) {
        return myPostRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "allPosts")
    public List<MyPost> getAllPosts() {
        return myPostRepository.findAll();
    }

    @Transactional
    public PostDto getNextPost(Long lastPostId) {
        MyPost nextPostEntity;

        if (lastPostId == null || lastPostId == 0) {
            // Начало ленты: просто самый новый
            nextPostEntity = myPostRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
        } else {
            // 1. Находим текущий пост-ориентир
            MyPost lastPost = myPostRepository.findById(lastPostId).orElse(null);

            if (lastPost != null) {
                // 2. Ищем строго следующий «вниз» по времени и ID
                nextPostEntity = myPostRepository.findFirstByCreatedAtBeforeOrCreatedAtAndIdLessThanOrderByCreatedAtDescIdDesc(
                        lastPost.getCreatedAt(),
                        lastPost.getCreatedAt(),
                        lastPost.getId()
                ).orElse(null);
            } else {
                nextPostEntity = null;
            }
        }

        return (nextPostEntity != null) ? convertToDto(nextPostEntity) : null;
    }

    // Исправлено: принимаем Long, так как в репозитории и БД это Long
    public void incrementLikeCount(Long postId) {
        myPostRepository.incrementLikeCount(postId);
    }

    private PostDto convertToDto(MyPost myPost) {
        MyUser user = myPost.getUser();

        List<CommentDto> commentsDtos = myCommentRepository.findMyCommentsByPost(myPost)
                .stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());

        return new PostDto(
                myPost.getId(),
                myPost.getContent(),
                myPost.getCreatedAt(), // УБРАЛ .toString()
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

    private CommentDto convertCommentToDto(MyComment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getText(),
                comment.getCreatedAt() // УБРАЛ .toString()
        );
    }
}