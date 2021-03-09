package an.evdokimov.discount.watcher.server.service;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import an.evdokimov.discount.watcher.server.service.user.UserService;
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
    private ModelMapper mapper;

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
}