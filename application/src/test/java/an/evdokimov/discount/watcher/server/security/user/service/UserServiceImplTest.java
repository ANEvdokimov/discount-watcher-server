package an.evdokimov.discount.watcher.server.security.user.service;

import an.evdokimov.discount.watcher.server.security.exception.ServerAuthenticationException;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.security.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserServiceImpl.class)
class UserServiceImplTest {
    @MockBean
    private UserRepository repository;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    public void prepareMock() {
        user1 = User.builder()
                .login("login")
                .enabled(true)
                .role(UserRole.ROLE_USER)
                .build();

        user2 = User.builder()
                .login("login2")
                .enabled(true)
                .role(UserRole.ROLE_USER)
                .build();

        when(repository.findById(any())).thenReturn(Optional.empty());
        when(repository.findById(user1.getLogin())).thenReturn(Optional.of(user1));
        when(repository.findById(user2.getLogin())).thenReturn(Optional.of(user2));

        when(repository.save(any())).thenAnswer(a -> a.getArgument(0));
        doThrow(DuplicateKeyException.class).when(repository).save(user1);
        doThrow(DuplicateKeyException.class).when(repository).save(user2);
    }

    @Test
    @DisplayName("Get user by login from repository")
    void get_existentUser_correctUser() {
        User actual = userService.get(user1.getLogin());

        assertEquals(user1, actual);
    }

    @Test
    @DisplayName("Get nonexistent user by login from repository")
    void get_nonexistentUser_null() {
        User actual = userService.get("ghost");

        assertNull(actual);
    }

    @Test
    @DisplayName("Create new user")
    void create_newUser_ok() {
        User newUser = User.builder()
                .login("new_user")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        userService.create(newUser);

        verify(repository).save(any());
        verify(repository).save(newUser);
    }

    @Test
    @DisplayName("Create duplicate user")
    void create_duplicateLogin_exception() {
        assertThrows(
                ServerAuthenticationException.class,
                () -> userService.create(user1)
        );
    }

    @Test
    @DisplayName("Get or create existent user")
    void getOrCreate_existentUser_user1() {
        User actual = userService.getOrCreate(user1.getLogin());

        assertEquals(user1, actual);
        verify(repository).findById(user1.getLogin());
        verify(repository, never()).save(user1);
    }

    @Test
    @DisplayName("Get or create new user")
    void getOrCreate_newUser_newUser() {
        User expected = User.builder()
                .login("ghost")
                .enabled(true)
                .role(UserRole.ROLE_USER)
                .build();

        User actual = userService.getOrCreate(expected.getLogin());

        assertEquals(expected, actual);
        verify(repository).findById(expected.getLogin());
        verify(repository).save(eq(expected));
    }

    @Test
    @DisplayName("Load UserDetails by login from repository")
    void loadUserByUsername_existentUser_correctUser() {
        UserDetails actual = userService.loadUserByUsername(user2.getLogin());

        assertEquals(user2, actual);
    }

    @Test
    @DisplayName("Load nonexistent UserDetails by login from repository")
    void loadUserByUsername_nonexistentUser_null() {
        UserDetails actual = userService.loadUserByUsername("ghost");

        assertNull(actual);
    }
}