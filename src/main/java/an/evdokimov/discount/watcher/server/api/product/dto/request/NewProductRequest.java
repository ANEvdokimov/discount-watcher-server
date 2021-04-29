package an.evdokimov.discount.watcher.server.api.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductRequest {
    @NotBlank
    private URL url;
    @NotNull
    private Long shopId;
}
