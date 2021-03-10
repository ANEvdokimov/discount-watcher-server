package an.evdokimov.discount.watcher.server.database.user.repository;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
}
