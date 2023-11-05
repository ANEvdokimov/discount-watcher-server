package an.evdokimov.discount.watcher.server.security.user.service;

import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
