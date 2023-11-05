package an.evdokimov.discount.watcher.server.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationExceptionCode {
    PRINCIPAL_NOT_FOUND("Principal not found in request"),
    USER_ALREADY_EXISTS("A user with this login already exists");

    private final String message;


    public void throwException() {
        throw new ServerAuthenticationException(this);
    }

    public void throwException(String details) {
        throw new ServerAuthenticationException(this, details);
    }

    public ServerAuthenticationException getException() {
        return new ServerAuthenticationException(this);
    }

    public ServerAuthenticationException getException(String details) {
        return new ServerAuthenticationException(this, details);
    }
}
