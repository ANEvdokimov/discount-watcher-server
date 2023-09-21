package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.amqp.repository.ParserService;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductInformationRepository productInformationRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;
    private final UserProductMapper userProductMapper;
    private final ParserService parserService;

    @Override
    public ProductResponse getProduct(@NotNull Long id, boolean withPriceHistory) throws ServerException {
        Product product;
        if (withPriceHistory) {
            product = productRepository.findById(id)
                    .orElseThrow(() -> new ServerException(ServerErrorCode.PRODUCT_NOT_FOUND));
        } else {
            product = productRepository.findByIdWithLastPrice(id)
                    .orElseThrow(() -> new ServerException(ServerErrorCode.PRODUCT_NOT_FOUND));
        }

        return productMapper.map(product);
    }

    @Override
    public Collection<ProductResponse> getUserProducts(@NotNull User user,
                                                       boolean withPriceHistory,
                                                       boolean onlyActive,
                                                       @Nullable Boolean monitorAvailability,
                                                       @Nullable Boolean monitorDiscount,
                                                       @Nullable Boolean monitorPriceChanges) {
        Collection<Product> userProducts;
        if (withPriceHistory) {
            if (onlyActive) {
                userProducts = productRepository.findActiveUserProducts(user, monitorAvailability, monitorDiscount,
                        monitorPriceChanges);
            } else {
                userProducts = productRepository.findAllUserProducts(user, monitorAvailability, monitorDiscount,
                        monitorPriceChanges);
            }
        } else {
            if (onlyActive) {
                userProducts = productRepository.findActiveUserProductsWithLastPrice(user, monitorAvailability,
                        monitorDiscount, monitorPriceChanges);
            } else {
                userProducts = productRepository.findAllUserProductsWithLastPrice(user, monitorAvailability,
                        monitorDiscount, monitorPriceChanges);
            }
        }

        return userProducts.stream()
                .map(productMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProductResponse> getUserProductsInShop(@NotNull User user,
                                                             @NotNull Long shopId,
                                                             boolean withPriceHistory,
                                                             boolean onlyActive,
                                                             @Nullable Boolean monitorAvailability,
                                                             @Nullable Boolean monitorDiscount,
                                                             @Nullable Boolean monitorPriceChanges)
            throws ServerException {
        Shop shop =
                shopRepository.findById(shopId).orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        Collection<Product> userProducts;
        if (withPriceHistory) {
            if (onlyActive) {
                userProducts = productRepository.findActiveUserProductsInShop(user, shop, monitorAvailability,
                        monitorDiscount, monitorPriceChanges);
            } else {
                userProducts = productRepository.findAllUserProductsInShop(user, shop, monitorAvailability,
                        monitorDiscount, monitorPriceChanges);
            }
        } else {
            if (onlyActive) {
                userProducts = productRepository.findActiveUserProductsWithLastPriceInShop(user, shop,
                        monitorAvailability, monitorDiscount, monitorPriceChanges);
            } else {
                userProducts = productRepository.findAllUserProductsWithLastPriceInShop(user, shop,
                        monitorAvailability, monitorDiscount, monitorPriceChanges);
            }
        }

        return userProducts.stream()
                .map(productMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void addProduct(@NotNull User user, @NotNull NewProductRequest newProduct)
            throws ServerException {
        Shop shop = shopRepository.findById(newProduct.getShopId())
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        ProductInformation information = productInformationRepository.findOrCreateByUrl(newProduct.getUrl());
        Product product = productRepository.findOrCreateByProductInformationAndShop(information, shop);

        ProductPrice price = productPriceMapper.mapNewPrice(product);
        productPriceRepository.save(price);
        product.addPrice(price);

        userProductRepository.saveOrUpdate(userProductMapper.map(newProduct, user, product));

        parserService.parseProduct(
                new ProductForParsing(
                        information.getId(),
                        price.getId(),
                        information.getUrl(),
                        shop.getCookie()
                )
        );
    }

    @Override
    @Transactional
    public void addProduct(@NotNull User user, @NotNull NewProductWithCookiesRequest newProduct)
            throws ServerException {
        Shop shop = shopRepository.findByCookie(newProduct.getCookies())
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        ProductInformation information = productInformationRepository.findOrCreateByUrl(newProduct.getUrl());
        Product product = productRepository.findOrCreateByProductInformationAndShop(information, shop);

        ProductPrice price = productPriceMapper.mapNewPrice(product);
        productPriceRepository.save(price);
        product.addPrice(price);

        userProductRepository.saveOrUpdate(userProductMapper.map(newProduct, user, product));

        parserService.parseProduct(
                new ProductForParsing(
                        information.getId(),
                        price.getId(),
                        information.getUrl(),
                        shop.getCookie()
                )
        );
    }

    @Override
    @Transactional
    public void updateProduct(@NotNull Product product) {
        ProductPrice productPrice = productPriceMapper.mapNewPrice(product);

        productPriceRepository.save(productPrice);
        product.addPrice(productPrice);

        parserService.parseProduct(
                new ProductForParsing(
                        product.getProductInformation().getId(),
                        productPrice.getId(),
                        product.getProductInformation().getUrl(),
                        product.getShop().getCookie()
                )
        );
    }
}
