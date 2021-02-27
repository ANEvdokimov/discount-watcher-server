package an.evdokimov.discount.watcher.server.api.user.service;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.session.dto.response.LogInDtoResponse;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterDtoRequest;
import an.evdokimov.discount.watcher.server.database.session.model.Session;
import an.evdokimov.discount.watcher.server.database.session.repository.SessionRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, SessionRepository sessionRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.modelMapper = modelMapper;
    }

    public LogInDtoResponse register(RegisterDtoRequest request) throws ServerException {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new ServerException(ServerErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = modelMapper.map(request, User.class);
        newUser.setRegisterDate(LocalDateTime.now());
        userRepository.save(newUser);

        Session session = Session.builder()
                .user(newUser)
                .lastCallTime(LocalDateTime.now())
                .token(UUID.randomUUID().toString())
                .build();
        sessionRepository.save(session);

        return modelMapper.map(session, LogInDtoResponse.class);
    }
}
