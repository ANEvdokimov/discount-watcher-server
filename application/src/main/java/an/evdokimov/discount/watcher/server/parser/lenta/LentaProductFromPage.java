package an.evdokimov.discount.watcher.server.parser.lenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LentaProductFromPage {
    private String title;
    private String subTitle;
    private Price regularPrice;
    private Price cardPrice;
    private int stock;
    private boolean hasDiscount;
    private String promoPercent;
    private String promoStart;
    private String promoEnd;
}

@Data
class Price {
    private BigDecimal value;
}
