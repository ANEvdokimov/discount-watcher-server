package an.evdokimov.discount.watcher.server.api.error.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about an error during validation of DTO.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    /**
     * Error description.
     */
    private String message;

    /**
     * A DTO field with an incorrect value.
     */
    private String field;
}
