package an.evdokimov.discount.watcher.server.database.session.repository;

import an.evdokimov.discount.watcher.server.database.session.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
}
