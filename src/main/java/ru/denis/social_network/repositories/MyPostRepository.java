package ru.denis.social_network.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyPost;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MyPostRepository extends JpaRepository<MyPost, Long> {

    // Берем самый новый пост для начала ленты
    Optional<MyPost> findFirstByOrderByCreatedAtDesc();

    // Берем следующий пост, который был создан РАНЬШЕ (Desc), чем текущий
    Optional<MyPost> findFirstByCreatedAtBeforeOrCreatedAtAndIdLessThanOrderByCreatedAtDescIdDesc(
            LocalDateTime createdAt,
            LocalDateTime createdAtSame,
            Long id
    );

    @Transactional
    @Modifying
    @Query("UPDATE MyPost p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(Long postId); // Изменил int на Long для соответствия ID
}