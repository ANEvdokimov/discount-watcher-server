package an.evdokimov.securitystarter.security.authentication;

import lombok.Getter;

public enum AuthenticationErrorMessage {
    SESSION_NOT_FOUND("Session with this token not found."),
    SESSION_IS_EXPIRED("The session is expired."),
    BAD_TOKEN("The token is invalid or does not exist");

    @Getter
    private final String message;

    AuthenticationErrorMessage(String message) {
        this.message = message;
    }
}
