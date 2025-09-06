package ru.denis.social_network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.denis.social_network.models.MyUser;
import ru.denis.social_network.repositories.MyUserRepository;
import ru.denis.social_network.services.MyUserService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SocialNetworkApplicationTests {

    @Mock
    private MyUserRepository myUserRepository;

    @InjectMocks
    private MyUserService myUserService;

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        int userId = 1;
        MyUser user = new MyUser(userId, "kadir123@gmail.com", "Kadirov Denis", "denis123", "denis");

        when(myUserRepository.findMyUserById(userId)).thenReturn(Optional.of(user));


        MyUser actualUser = myUserService.getUserById(userId);

        assertEquals(userId, actualUser.getId());
        assertEquals("kadir123@gmail.com", actualUser.getEmail());
        assertEquals("Kadirov Denis", actualUser.getName());
        assertEquals("denis123", actualUser.getPassword());
        assertEquals("denis", actualUser.getNickname());

        verify(myUserRepository, times(1)).findMyUserById(userId);
    }
    public MyUser getUserByUsername(String username) {
        MyUser user = myUserRepository.findMyUserByEmail(username).orElse(null);

        return user;
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        String email = "kadir123@gmail.com";
        MyUser user = new MyUser(1, "kadir123@gmail.com", "Kadirov Denis", "denis123", "denis");

        when(myUserRepository.findMyUserByEmail(email)).thenReturn(Optional.of(user));


        MyUser actualUser = myUserService.getUserByUsername(user.getEmail());


        assertNotNull(actualUser);
        assertEquals("kadir123@gmail.com", actualUser.getEmail());
        assertEquals("Kadirov Denis", actualUser.getName());
        assertEquals("denis123", actualUser.getPassword());
        assertEquals("denis", actualUser.getNickname());

        verify(myUserRepository, times(1)).findMyUserByEmail(user.getEmail());
    }


}
