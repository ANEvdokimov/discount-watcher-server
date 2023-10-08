package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {
                ProductInformationMapper.class,
                ProductPriceMapper.class,
                ShopMapper.class
        }
)
public abstract class ProductMapper {
    @Autowired
    private ProductInformationMapper productInformationMapper;
    @Autowired
    private ProductPriceMapper productPriceMapper;
    @Autowired
    private ShopMapper shopMapper;

    public ProductResponse map(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .productInformation(productInformationMapper.map(product.getProductInformation()))
                .shop(shopMapper.map(product.getShop()))
                .lastPrice(productPriceMapper.map(product.getLastPrice()))
                .build();
    }
}
