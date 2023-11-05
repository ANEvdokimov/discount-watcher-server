package an.evdokimov.discount.watcher.server.security.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServerAuthenticationException extends RuntimeException {
    private final AuthenticationExceptionCode code;
    private final String details;

    public ServerAuthenticationException(@NotNull AuthenticationExceptionCode code, @NotNull String details, @NotNull Throwable cause) {
        super("%s DETAILS: %s".formatted(code.getMessage(), details), cause);
        this.code = code;
        this.details = details;
    }

    public ServerAuthenticationException(@NotNull AuthenticationExceptionCode code, @NotNull String details) {
        super("%s DETAILS: %s".formatted(code.getMessage(), details));
        this.code = code;
        this.details = details;
    }

    public ServerAuthenticationException(@NotNull AuthenticationExceptionCode code, @NotNull Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
        this.details = null;
    }

    public ServerAuthenticationException(@NotNull AuthenticationExceptionCode code) {
        super(code.getMessage());
        this.code = code;
        this.details = null;
    }
}
