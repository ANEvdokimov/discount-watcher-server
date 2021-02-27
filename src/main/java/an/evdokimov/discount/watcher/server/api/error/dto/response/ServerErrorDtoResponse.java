package an.evdokimov.discount.watcher.server.api.error.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerErrorDtoResponse {
    private String message;
}
