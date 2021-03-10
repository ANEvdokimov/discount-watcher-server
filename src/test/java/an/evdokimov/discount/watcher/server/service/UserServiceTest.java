package an.evdokimov.discount.watcher.server.service;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.LoginRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ModelMapper mapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void register_validDto_logInDtoResponse() throws ServerException {
        RegisterRequest newUserDto = RegisterRequest.builder()
                .name("new user")
                .login("new user")
                .password("pass")
                .build();

        when(mapper.map(newUserDto, User.class)).thenReturn(User.builder().password("123123").build());
        when(userRepository.findByLogin(newUserDto.getLogin())).thenReturn(Optional.empty());

        userService.register(newUserDto);

        assertAll(
                () -> verify(userRepository, times(1)).findByLogin(newUserDto.getLogin()),
                () -> verify(userRepository, times(1)).save(any()),
                () -> verify(mapper, times(1)).map(newUserDto, User.class)
        );
    }

    @Test
    void register_existingLogin_serverException() {
        RegisterRequest newUserDto = RegisterRequest.builder()
                .name("new user")
                .login("new user")
                .password("pass")
                .build();

        when(userRepository.findByLogin(newUserDto.getLogin())).thenReturn(Optional.of(new User()));

        assertAll(
                () -> assertThrows(ServerException.class, () -> userService.register(newUserDto)),
                () -> verify(userRepository, times(1)).findByLogin(newUserDto.getLogin()),
                () -> verify(userRepository, times(0)).save(any())
        );
    }

    @Test
    void login_existingUser_logInDtoResponse() {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("pass")
                .build();

        User userFromDb = User.builder()
                .login("user")
                .password("hash pass")
                .build();

        when(userRepository.findByLogin(request.getLogin())).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(request.getPassword(), userFromDb.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(userFromDb.getLogin())).thenReturn("token");

        assertAll(
                () -> assertEquals("token", userService.login(request).getToken()),
                () -> verify(passwordEncoder, times(1))
                        .matches(request.getPassword(), userFromDb.getPassword()),
                () -> verify(userRepository, times(1)).findByLogin(userFromDb.getLogin()),
                () -> verify(jwtUtils, times(1)).generateToken(userFromDb.getLogin())
        );
    }

    @Test
    void login_notExistentLogin_ServerException() {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("pass")
                .build();

        when(userRepository.findByLogin(request.getLogin())).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> userService.login(request)),
                () -> verify(userRepository, times(1)).findByLogin(request.getLogin())
        );
    }

    @Test
    void login_wrongPassword_ServerException() {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("wrong pass")
                .build();

        User userFromDb = User.builder()
                .login("user")
                .password("hash pass")
                .build();

        when(userRepository.findByLogin(userFromDb.getLogin())).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(request.getPassword(), userFromDb.getPassword())).thenReturn(false);

        assertAll(
                () -> assertThrows(ServerException.class, () -> userService.login(request)),
                () -> verify(passwordEncoder, times(1))
                        .matches(request.getPassword(), userFromDb.getPassword()),
                () -> verify(userRepository, times(1)).findByLogin(request.getLogin())
        );
    }
}