package an.evdokimov.discount.watcher.server.service.user;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.LoginRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LoginResponse;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.model.UserRole;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import an.evdokimov.discount.watcher.server.mapper.user.UserMapper;
import an.evdokimov.securitystarter.security.authentication.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final Clock clock;

    @Override
    public LoginResponse register(RegisterRequest request) throws ServerException {
        log.debug("registration user {}", request.getLogin());

        Optional<User> userFromDB = userRepository.findByLogin(request.getLogin());
        if (userFromDB.isPresent()) {
            log.warn(ServerErrorCode.USER_ALREADY_EXISTS + ". login: {}", request.getLogin());
            throw new ServerException(ServerErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = mapper.fromDto(request);
        newUser.setRole(UserRole.ROLE_USER);
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        newUser.setRegisterDate(LocalDateTime.now(clock));
        userRepository.save(newUser);

        return new LoginResponse(jwtUtils.generateToken(newUser.getLogin()));
    }

    @Override
    public LoginResponse login(LoginRequest request) throws ServerException {
        log.debug("login user {}", request.getLogin());

        User userFromDb = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD));

        if (!encoder.matches(request.getPassword(), userFromDb.getPassword())) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }

        return new LoginResponse(jwtUtils.generateToken(userFromDb.getLogin()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("getting user by login {}", username);

        return userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
