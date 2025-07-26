package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyComment;
import ru.denis.social_network.models.MyPost;

import java.util.List;

@Repository
public interface MyCommentRepository extends JpaRepository<MyComment, Integer> {
    List<MyComment> findMyCommentsByPost(MyPost post);
}
