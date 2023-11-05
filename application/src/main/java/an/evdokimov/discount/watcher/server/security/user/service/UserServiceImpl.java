package an.evdokimov.discount.watcher.server.security.user.service;

import an.evdokimov.discount.watcher.server.security.exception.AuthenticationExceptionCode;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.security.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User get(String login) {
        return repository.findById(login).orElse(null);
    }

    @Override
    public User create(User user) {
        try {
            return repository.save(user);
        } catch (DuplicateKeyException e) {
            log.error(AuthenticationExceptionCode.USER_ALREADY_EXISTS.getMessage(), e);
            AuthenticationExceptionCode.USER_ALREADY_EXISTS.throwException("login=" + user.getLogin());
            return null;
        }
    }

    @Override
    public User getOrCreate(String login) {
        return repository.findById(login).orElseGet(() ->
                create(User.builder()
                        .login(login)
                        .role(UserRole.ROLE_USER)
                        .enabled(true)
                        .build()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return get(username);
    }
}
