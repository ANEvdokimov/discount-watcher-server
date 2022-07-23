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
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.parser.Parser;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ParserFactory parserFactory;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductInformationRepository productInformationRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ParserFactory parserFactory, ProductRepository productRepository,
                              ProductPriceRepository productPriceRepository,
                              ProductInformationRepository productInformationRepository, ShopRepository shopRepository,
                              ProductMapper productMapper) {
        this.parserFactory = parserFactory;
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.productInformationRepository = productInformationRepository;
        this.shopRepository = shopRepository;
        this.productMapper = productMapper;
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
    public Collection<ProductResponse> getUserProducts(@NotNull User user, boolean withPriceHistory,
                                                       boolean onlyActive) {
        Collection<Product> userProducts;
        if (withPriceHistory) {
            if (onlyActive) {
                userProducts = productRepository.findAllActiveUsersProducts(user);
            } else {
                userProducts = productRepository.findAllUsersProducts(user);
            }
        } else {
            if (onlyActive) {
                userProducts = productRepository.findAllActiveUsersProductsWithLastPrice(user);
            } else {
                userProducts = productRepository.findAllUsersProductsWithLastPrice(user);
            }
        }
        return userProducts.stream()
                .map(productMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProductResponse> getUserProductsInShop(@NotNull User user, @NotNull Long shopId,
                                                             boolean withPriceHistory, boolean onlyActive)
            throws ServerException {
        Shop shop =
                shopRepository.findById(shopId).orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        Collection<Product> userProducts;
        if (withPriceHistory) {
            if (onlyActive) {
                userProducts = productRepository.findAllActiveUsersProductsInShop(user, shop);
            } else {
                userProducts = productRepository.findAllUsersProductsInShop(user, shop);
            }
        } else {
            if (onlyActive) {
                userProducts = productRepository.findAllActiveUserProductsWithLastPriceInShop(user, shop);
            } else {
                userProducts = productRepository.findAllUsersProductsWithLastPriceInShop(user, shop);
            }
        }

        return userProducts.stream()
                .map(productMapper::map)
                .toList();
    }

    @Override
    public ProductResponse addProduct(@NotNull NewProductRequest newProduct) throws ServerException {
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
