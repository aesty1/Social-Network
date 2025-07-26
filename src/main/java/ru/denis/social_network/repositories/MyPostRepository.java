package ru.denis.social_network.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyPost;

import java.util.Optional;

@Repository
public interface MyPostRepository extends JpaRepository<MyPost, Long> {
    Optional<MyPost> findFirstByOrderByCreatedAtDesc();
    Optional<MyPost> findFirstByIdLessThanOrderByCreatedAtDesc(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE MyPost p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(int postId);
}
