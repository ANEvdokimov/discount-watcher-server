package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.amqp.service.ParserService;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.mapper.product.ParsedProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.ProductInformationService;
import an.evdokimov.discount.watcher.server.service.product.ProductPriceService;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import an.evdokimov.discount.watcher.server.service.product.UserProductService;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Maintenance
@Slf4j
public class ProductMaintenanceImpl implements ProductMaintenance {
    private final ProductService productService;
    private final ProductInformationService informationService;
    private final ProductPriceService priceService;
    private final UserProductService userProductService;
    private final ParserService parserService;
    private final ShopService shopService;
    private final ProductMapper productMapper;
    private final ProductPriceMapper priceMapper;
    private final ParsedProductPriceMapper parsedProductPriceMapper;
    private final UserProductMapper userProductMapper;

    private final ProductMaintenanceImpl self;

    private final Clock clock;

    public ProductMaintenanceImpl(ProductService productService,
                                  ProductInformationService informationService,
                                  ProductPriceService priceService,
                                  UserProductService userProductService,
                                  ParserService parserService,
                                  ShopService shopService,
                                  ProductMapper productMapper,
                                  ProductPriceMapper priceMapper,
                                  ParsedProductPriceMapper parsedProductPriceMapper,
                                  UserProductMapper userProductMapper,
                                  @Lazy ProductMaintenanceImpl self,
                                  Clock clock) {
        this.productService = productService;
        this.informationService = informationService;
        this.priceService = priceService;
        this.userProductService = userProductService;
        this.parserService = parserService;
        this.shopService = shopService;
        this.productMapper = productMapper;
        this.priceMapper = priceMapper;
        this.parsedProductPriceMapper = parsedProductPriceMapper;
        this.userProductMapper = userProductMapper;
        this.self = self;
        this.clock = clock;
    }

    @Override
    @NotNull
    public ProductResponse getById(@NotNull Long id) throws ServerException {
        log.trace("getting product by id={}", id);
        return productMapper.map(productService.getById(id));
    }

    @Override
    @Transactional
    public void saveProduct(@NotNull User user, @NotNull NewProductRequest newProduct) throws ServerException {
        Shop shop = shopService.getById(newProduct.getShopId());

        ProductInformation information = informationService.getOrCreateByUrl(newProduct.getUrl());
        Product product = productService.getOrCreateByProductInformationAndShop(information, shop);
        ProductPrice price = priceMapper.mapNewPrice(product, LocalDateTime.now(clock));

        priceService.savePrice(price);
        product.addPrice(price);

        userProductService.saveOrUpdate(userProductMapper.map(newProduct, user, product));

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
    public void saveProduct(@NotNull User user, @NotNull NewProductWithCookiesRequest newProduct) throws ServerException {
        Shop shop = shopService.getByCookie(newProduct.getCookies());

        ProductInformation information = informationService.getOrCreateByUrl(newProduct.getUrl());
        Product product = productService.getOrCreateByProductInformationAndShop(information, shop);
        ProductPrice price = priceMapper.mapNewPrice(product, LocalDateTime.now(clock));

        priceService.savePrice(price);
        product.addPrice(price);

        userProductService.saveOrUpdate(userProductMapper.map(newProduct, user, product));

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
    public void saveParsedProduct(@NotNull ParsedProductInformation parsedProduct) throws ServerException {
        ParsedProductPrice parsedPrice = parsedProduct.getProductPrice();
        ProductPrice priceInDb = priceService.getById(parsedPrice.getId());
        parsedProductPriceMapper.updateNotNullFields(parsedPrice, priceInDb);

        priceService.findLastCompletedPriceByProduct(priceInDb.getProduct())
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

        if (StringUtils.isNotBlank(parsedProduct.getName())) {
            ProductInformation information = informationService.getById(parsedProduct.getId());
            if (!parsedProduct.getName().equals(information.getName())
                    || !information.getParsingStatus().equals(ParsingStatus.COMPLETE)) {
                information.setName(parsedProduct.getName());
                information.setParsingStatus(ParsingStatus.COMPLETE);
            }
        }
    }

    @Override
    @Transactional
    public void update(@NotNull Long id) throws ServerException {
        self.updateProduct(productService.getById(id));
    }

    @Override
    @Transactional
    public void updateTrackedProducts() {
        productService.getAllTrackedProducts().forEach(self::updateProduct);
    }

    @Transactional
    public void updateProduct(Product product) {
        ProductPrice productPrice = priceMapper.mapNewPrice(product, LocalDateTime.now(clock));

        priceService.savePrice(productPrice);
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
