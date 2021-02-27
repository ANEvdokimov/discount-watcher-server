package an.evdokimov.discount.watcher.server.api.session.service;

import an.evdokimov.discount.watcher.server.database.session.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
//
//    public LogInDtoResponse logIn(){
//
//    }
}
