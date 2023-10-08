package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public abstract class ProductPriceMapper {
    private final Map<Class<? extends ProductPrice>, Function<ProductPrice, ProductPriceResponse>> productMappers;

    protected ProductPriceMapper() {
        productMappers = Map.of(
                ProductPrice.class, this::mapProductPrice,
                LentaProductPrice.class, this::mapLentaProductPrice
        );
    }

    public ProductPriceResponse map(ProductPrice productPrice) {
        return productMappers.get(productPrice.getClass()).apply(productPrice);
    }

    abstract ProductPriceResponse mapProductPrice(ProductPrice productPrice);

    private LentaProductPriceResponse mapLentaProductPrice(ProductPrice productPrice) {
        return mapLentaProductPrice((LentaProductPrice) productPrice);
    }

    abstract LentaProductPriceResponse mapLentaProductPrice(LentaProductPrice productPrice);

    public ProductPrice mapNewPrice(Product product, LocalDateTime creationDate) { //todo refactor
        if (product.getProductInformation().getUrl().getHost().equals("lenta.com")) {
            return LentaProductPrice.builder()
                    .product(product)
                    .creationDate(creationDate)
                    .parsingStatus(ParsingStatus.PROCESSING)
                    .build();
        } else {
            return ProductPrice.builder()
                    .product(product)
                    .creationDate(creationDate)
                    .parsingStatus(ParsingStatus.PROCESSING)
                    .build();
        }
    }
}
