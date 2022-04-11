package an.evdokimov.discount.watcher.server.api.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
