package an.evdokimov.discount.watcher.server.api.error.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about server error.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerErrorResponse {
    /**
     * Error description.
     */
    private String message;
}
