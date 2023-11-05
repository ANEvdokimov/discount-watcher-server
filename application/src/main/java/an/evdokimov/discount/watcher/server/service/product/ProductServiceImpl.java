package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.amqp.service.ParserService;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.mapper.product.ParsedProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductInformationRepository productInformationRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;
    private final UserProductMapper userProductMapper;
    private final ParsedProductPriceMapper parsedProductPriceMapper;
    private final ParserService parserService;
    private final Clock clock;

    private final ProductServiceImpl self;


    public ProductServiceImpl(ProductRepository productRepository,
                              UserProductRepository userProductRepository,
                              ProductPriceRepository productPriceRepository,
                              ProductInformationRepository productInformationRepository,
                              ShopRepository shopRepository,
                              ProductMapper productMapper,
                              ProductPriceMapper productPriceMapper,
                              UserProductMapper userProductMapper,
                              ParsedProductPriceMapper parsedProductPriceMapper,
                              ParserService parserService,
                              Clock clock,
                              @Lazy ProductServiceImpl productService) {
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.productPriceRepository = productPriceRepository;
        this.productInformationRepository = productInformationRepository;
        this.shopRepository = shopRepository;
        this.productMapper = productMapper;
        this.productPriceMapper = productPriceMapper;
        this.userProductMapper = userProductMapper;
        this.parsedProductPriceMapper = parsedProductPriceMapper;
        this.parserService = parserService;
        this.clock = clock;
        this.self = productService;
    }

    @Override
    public ProductResponse getProduct(@NotNull Long id) throws ServerException {
        Product product;
        product = productRepository.findById(id)
                .orElseThrow(() -> new ServerException(ServerErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.map(product);
    }

    @Override
    @Transactional
    public void addProduct(@NotNull User user, @NotNull NewProductRequest newProduct)
            throws ServerException {
        Shop shop = shopRepository.findById(newProduct.getShopId())
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        ProductInformation information = productInformationRepository.findOrCreateByUrl(newProduct.getUrl());
        Product product = productRepository.findOrCreateByProductInformationAndShop(information, shop);

        ProductPrice price = productPriceMapper.mapNewPrice(product, LocalDateTime.now(clock));
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

        ProductPrice price = productPriceMapper.mapNewPrice(product, LocalDateTime.now(clock));
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
    public void updateProduct(@NotNull Long productId) throws ServerException {
        self.updateProduct(productRepository.findById(productId)
                .orElseThrow(() -> ServerErrorCode.PRODUCT_NOT_FOUND.getException("Product id: " + productId)));
    }

    @Override
    @Transactional
    public void updateProduct(@NotNull Product product) {
        ProductPrice productPrice = productPriceMapper.mapNewPrice(product, LocalDateTime.now(clock));

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

    @Override
    @Transactional
    public void saveParsedProduct(@NotNull ParsedProductInformation parsedProduct) throws ServerException {
        ParsedProductPrice parsedPrice = parsedProduct.getProductPrice();
        ProductPrice priceInDb = productPriceRepository
                .findById(parsedPrice.getId())
                .orElseThrow(() -> ServerErrorCode.PRODUCT_PRICE_NOT_FOUND.getException(parsedProduct.toString()));
        parsedProductPriceMapper.updateNotNullFields(parsedPrice, priceInDb);

        productPriceRepository.findLastCompletedPriceByProduct(priceInDb.getProduct())
                .ifPresentOrElse(
                        previousPrice -> {
                            BigDecimal previous = previousPrice.getPriceWithDiscount() != null ?
                                    previousPrice.getPriceWithDiscount() : previousPrice.getPrice();
                            BigDecimal actual = priceInDb.getPriceWithDiscount() != null ?
                                    priceInDb.getPriceWithDiscount() : priceInDb.getPrice();

                            if (actual == null) priceInDb.setPriceChange(PriceChange.UNDEFINED);
                            else if (previous.equals(actual)) priceInDb.setPriceChange(PriceChange.EQUAL);
                            else if (previous.compareTo(actual) < 0) priceInDb.setPriceChange(PriceChange.UP);
                            else if (previous.compareTo(actual) > 0) priceInDb.setPriceChange(PriceChange.DOWN);
                        },
                        () -> {
                            BigDecimal actual = priceInDb.getPriceWithDiscount() != null ?
                                    priceInDb.getPriceWithDiscount() : priceInDb.getPrice();
                            if (actual == null) priceInDb.setPriceChange(PriceChange.UNDEFINED);
                            else priceInDb.setPriceChange(PriceChange.FIRST_PRICE);
                        }
                );

        productPriceRepository.save(priceInDb);

        if (StringUtils.isNotBlank(parsedProduct.getName())) {
            if (productInformationRepository.updateNameById(parsedProduct.getId(), parsedProduct.getName()) != 1) {
                ServerErrorCode.PRODUCT_INFORMATION_NOT_FOUND.throwException(parsedProduct.toString());
            }
        }
    }
}
