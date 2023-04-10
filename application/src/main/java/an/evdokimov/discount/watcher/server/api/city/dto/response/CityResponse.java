package an.evdokimov.discount.watcher.server.api.city.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {
    private Long id;
    /**
     * A name of the city in English.
     */
    private String name;

    /**
     * A name of the city in Russian.
     */
    private String cyrillicName;
}
