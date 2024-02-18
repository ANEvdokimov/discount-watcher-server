package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @NotNull
    public Product getProduct(@NotNull Long id) throws ServerException {
        return productRepository.findById(id)
                .orElseThrow(() -> ServerErrorCode.PRODUCT_NOT_FOUND.getException("id=" + id));
    }

    @Override
    @NotNull
    @Transactional
    public Product getOrCreateByProductInformationAndShop(ProductInformation information, Shop shop) {
        return productRepository.findByProductInformationAndShop(information, shop)
                .orElseGet(() ->
                        productRepository.save(
                                Product.builder()
                                        .productInformation(information)
                                        .shop(shop)
                                        .build()
                        )
                );
    }

    @Override
    @NotNull
    public Collection<Product> getAllTrackedProducts() {
        return productRepository.findAllTrackedProducts();
    }

    @Override
    @Transactional
    public void addProduct(@NotNull Product product) {
        productRepository.save(product);
    }
}
