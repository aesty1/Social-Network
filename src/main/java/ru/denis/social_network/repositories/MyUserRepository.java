package ru.denis.social_network.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.social_network.models.MyUser;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findMyUserByEmail(String email);
    Optional<MyUser> findMyUserByNickname(String nickname);
}
