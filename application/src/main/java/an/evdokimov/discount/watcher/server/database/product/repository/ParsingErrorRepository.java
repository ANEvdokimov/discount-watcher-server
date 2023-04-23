package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.ParsingError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsingErrorRepository extends JpaRepository<ParsingError, Long> {
}
