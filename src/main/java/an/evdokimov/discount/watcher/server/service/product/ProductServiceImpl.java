package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.parser.Parser;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ParserFactory parserFactory;
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductInformationRepository productInformationRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final UserProductMapper userProductMapper;

    public ProductServiceImpl(ParserFactory parserFactory,
                              ProductRepository productRepository,
                              UserProductRepository userProductRepository,
                              ProductPriceRepository productPriceRepository,
                              ProductInformationRepository productInformationRepository,
                              ShopRepository shopRepository,
                              ProductMapper productMapper,
                              UserProductMapper userProductMapper) {
        this.parserFactory = parserFactory;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.productPriceRepository = productPriceRepository;
        this.productInformationRepository = productInformationRepository;
        this.shopRepository = shopRepository;
        this.productMapper = productMapper;
        this.userProductMapper = userProductMapper;
    }

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
    public ProductResponse addProduct(@NotNull User user, @NotNull NewProductRequest newProduct)
            throws ServerException {
        Shop shop = shopRepository.findById(newProduct.getShopId())
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        Parser parser;
        try {
            parser = parserFactory.getParser(newProduct.getUrl());
        } catch (ParserFactoryException e) {
            throw new ServerException(ServerErrorCode.UNSUPPORTED_SHOP, e);
        }

        Product parsedProduct;
        try {
            parsedProduct = parser.parse(newProduct.getUrl(), shop);
        } catch (PageDownloaderException e) {
            throw new ServerException(ServerErrorCode.PAGE_DOWNLOAD_ERROR, e);
        } catch (ParserException e) {
            throw new ServerException(ServerErrorCode.PARSE_PAGE_ERROR, e);
        }

        productInformationRepository.saveIfAbsent(parsedProduct.getProductInformation());
        productRepository.saveIfAbsent(parsedProduct);
        productPriceRepository.saveAll(parsedProduct.getPrices());
        userProductRepository.saveOrUpdate(userProductMapper.map(newProduct, user, parsedProduct));

        return productMapper.map(parsedProduct);
    }

    @Override
    public Product updateProduct(@NotNull Product product) throws ServerException {
        Parser parser;
        try {
            parser = parserFactory.getParser(product.getProductInformation().getUrl());
        } catch (ParserFactoryException e) {
            throw new ServerException(ServerErrorCode.UNSUPPORTED_SHOP, e);
        }

        ProductPrice parsedProductPrice;
        try {
            parsedProductPrice = parser.parse(product);
        } catch (PageDownloaderException e) {
            throw new ServerException(ServerErrorCode.PAGE_DOWNLOAD_ERROR, e);
        } catch (ParserException e) {
            throw new ServerException(ServerErrorCode.PARSE_PAGE_ERROR, e);
        }

        productPriceRepository.save(parsedProductPrice);
        product.addPrice(parsedProductPrice);
        return product;
    }
}
