package an.evdokimov.discount.watcher.server.api.user.service;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.session.dto.response.LogInDtoResponse;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterDtoRequest;
import an.evdokimov.discount.watcher.server.database.session.model.Session;
import an.evdokimov.discount.watcher.server.database.session.repository.SessionRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    void register_validDto_logInDtoResponse() throws ServerException {
        RegisterDtoRequest newUserDto = RegisterDtoRequest.builder()
                .name("new user")
                .login("new user")
                .password("pass")
                .build();

        when(modelMapper.map(newUserDto, User.class)).thenReturn(new User());
        when(userRepository.findByLogin(newUserDto.getLogin())).thenReturn(Optional.empty());

        userService.register(newUserDto);

        assertAll(
                () -> verify(userRepository, times(1)).findByLogin(newUserDto.getLogin()),
                () -> verify(userRepository, times(1)).save(any()),
                () -> verify(sessionRepository, times(1)).save(any()),
                () -> verify(modelMapper, times(1)).map(newUserDto, User.class),
                () -> verify(modelMapper, times(1))
                        .map(any(Session.class), eq(LogInDtoResponse.class))
        );
    }

    @Test
    void register_existingLogin_serverException() {
        RegisterDtoRequest newUserDto = RegisterDtoRequest.builder()
                .name("new user")
                .login("new user")
                .password("pass")
                .build();

        when(modelMapper.map(newUserDto, User.class)).thenReturn(new User());
        when(userRepository.findByLogin(newUserDto.getLogin())).thenReturn(Optional.of(new User()));

        assertAll(
                () -> assertThrows(ServerException.class, () -> userService.register(newUserDto)),
                () -> verify(userRepository, times(1)).findByLogin(newUserDto.getLogin()),
                () -> verify(userRepository, times(0)).save(any()),
                () -> verify(sessionRepository, times(0)).save(any()),
                () -> verify(modelMapper, times(0)).map(newUserDto, User.class),
                () -> verify(modelMapper, times(0))
                        .map(any(Session.class), eq(LogInDtoResponse.class))
        );
    }
}