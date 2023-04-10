package an.evdokimov.discount.watcher.server.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User's credentials.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /**
     * A user's login.
     */
    @NotBlank
    private String login;

    /**
     * A user's password.
     */
    @NotBlank
    private String password;
}
