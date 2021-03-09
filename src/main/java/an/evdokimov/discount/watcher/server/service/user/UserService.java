package an.evdokimov.discount.watcher.server.service.user;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LogInResponse;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.model.UserRole;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final ModelMapper mapper;

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;

    public UserService(ModelMapper mapper, UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder encoder) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    public LogInResponse register(RegisterRequest request) throws ServerException {
        Optional<User> userFromDB = userRepository.findByLogin(request.getLogin());
        if (userFromDB.isPresent()) {
            throw new ServerException(ServerErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = mapper.map(request, User.class);
        newUser.setRole(UserRole.ROLE_USER);
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        newUser.setRegisterDate(LocalDateTime.now());
        userRepository.save(newUser);

        return new LogInResponse(jwtUtils.generateToken(newUser.getLogin()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }
}
