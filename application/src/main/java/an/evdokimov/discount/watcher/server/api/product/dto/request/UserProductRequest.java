package an.evdokimov.discount.watcher.server.api.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductRequest {
    @NotNull
    private Long id;
    private Long productId;
    private boolean monitorDiscount;
    private boolean monitorAvailability;
    private boolean monitorPriceChanges;
}
