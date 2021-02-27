package an.evdokimov.discount.watcher.server.database.user.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String login;
    private String passwordHash;
    private String name;
    private LocalDateTime registerDate;
    private Boolean deleted;
}
