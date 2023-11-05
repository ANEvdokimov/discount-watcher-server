package an.evdokimov.discount.watcher.server.security.user.service;

import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User get(String login);

    User create(User user);

    User getOrCreate(String login);
}
