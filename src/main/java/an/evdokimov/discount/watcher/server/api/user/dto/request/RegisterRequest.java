package an.evdokimov.discount.watcher.server.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about new registered user.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    /**
     * User's login.
     */
    @NotBlank
    private String login;

    /**
     * User's password.
     */
    @NotBlank
    private String password;

    /**
     * User's name.
     */
    @NotBlank
    private String name;
}
