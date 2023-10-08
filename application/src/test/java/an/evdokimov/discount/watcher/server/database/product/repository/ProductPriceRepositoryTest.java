package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductPriceRepositoryTest {
    @Autowired
    CityRepository cityRepository;
    @Autowired
    private ShopChainRepository shopChainRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ProductInformationRepository informationRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPriceRepository testedPriceRepository;

    private Product mockedProduct;

    @BeforeAll
    void setUpData() throws MalformedURLException {
        cityRepository.deleteAll();
        shopChainRepository.deleteAll();
        shopRepository.deleteAll();
        informationRepository.deleteAll();
        productRepository.deleteAll();

        City city = City.builder()
                .name("city-17")
                .cyrillicName("city-17")
                .build();
        cityRepository.save(city);
        ShopChain shopChain = ShopChain.builder()
                .name("shop-chain-1")
                .build();
        shopChainRepository.save(shopChain);
        Shop shop = Shop.builder()
                .shopChain(shopChain)
                .city(city)
                .name("shop-name-1")
                .address("address-1")
                .build();
        shopRepository.save(shop);
        ProductInformation information = ProductInformation.builder()
                .parsingStatus(ParsingStatus.COMPLETE)
                .name("product-1")
                .url(new URL("https://url-1.test"))
                .build();
        informationRepository.save(information);
        mockedProduct = Product.builder()
                .productInformation(information)
                .shop(shop)
                .build();
        productRepository.save(mockedProduct);
    }

    @AfterEach
    void clearDb() {
        testedPriceRepository.deleteAll();
    }

    @Test
    void findById_LentaProductPrice_instanceOfLentaProductPrice() {
        LentaProductPrice lentaProductPrice = LentaProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithCard(BigDecimal.valueOf(500))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .build();
        LentaProductPrice savedLentaProductPrice = testedPriceRepository.save(lentaProductPrice);

        assertTrue(testedPriceRepository.findById(savedLentaProductPrice.getId()).get() instanceof LentaProductPrice);
    }

    @Test
    void findById_ProductPrice_instanceOfProductPrice() {
        ProductPrice productPrice = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .build();
        ProductPrice savedProductPrice = testedPriceRepository.save(productPrice);

        assertFalse(testedPriceRepository.findById(savedProductPrice.getId()).get() instanceof LentaProductPrice);
    }

    @Test
    void findLastPriceByProduct_noPreviousPrice_null() {
        assertTrue(testedPriceRepository.findLastCompletedPriceByProduct(mockedProduct).isEmpty());
    }

    @Test
    void findLastPriceByProduct_1PreviousPriceUndefined_previousPrice() {
        ProductPrice productPrice = ProductPrice.builder()
                .product(mockedProduct)
                .price(null)
                .priceWithDiscount(null)
                .creationDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();
        testedPriceRepository.save(productPrice);

        Optional<ProductPrice> result = testedPriceRepository.findLastCompletedPriceByProduct(mockedProduct);
        assertTrue(result.isEmpty());
    }

    @Test
    void findLastPriceByProduct_1PreviousPrice_previousPrice() {
        ProductPrice productPrice = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .creationDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .build();
        ProductPrice savedProductPrice = testedPriceRepository.save(productPrice);

        Optional<ProductPrice> result = testedPriceRepository.findLastCompletedPriceByProduct(mockedProduct);
        assertTrue(result.isPresent());
        assertEquals(savedProductPrice, result.get());
    }

    @Test
    void findLastPriceByProduct_2PreviousPrice_previousPrice() {
        ProductPrice productPrice1 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .creationDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .build();
        testedPriceRepository.save(productPrice1);

        ProductPrice productPrice2 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(2000))
                .priceWithDiscount(BigDecimal.valueOf(1000))
                .creationDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UP)
                .build();
        ProductPrice savedProductPrice = testedPriceRepository.save(productPrice2);

        Optional<ProductPrice> result = testedPriceRepository.findLastCompletedPriceByProduct(mockedProduct);
        assertTrue(result.isPresent());
        assertEquals(savedProductPrice, result.get());
    }

    @Test
    void findLastPriceByProduct_2PreviousPriceAndNull_previousPrice() {
        ProductPrice productPrice1 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .creationDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingDate(LocalDateTime.of(1998, 9, 26, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .build();
        ProductPrice savedProductPrice = testedPriceRepository.save(productPrice1);

        ProductPrice productPrice2 = ProductPrice.builder()
                .product(mockedProduct)
                .price(null)
                .priceWithDiscount(null)
                .creationDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2005, 3, 1, 0, 0))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();
        testedPriceRepository.save(productPrice2);

        Optional<ProductPrice> result = testedPriceRepository.findLastCompletedPriceByProduct(mockedProduct);
        assertTrue(result.isPresent());
        assertEquals(savedProductPrice, result.get());
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_twoPairsOfSamePrices_prices() {
        ProductPrice price1 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        testedPriceRepository.save(price1);
        ProductPrice price2 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();
        testedPriceRepository.save(price2);

        ProductPrice price3 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .creationDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .build();
        testedPriceRepository.save(price3);
        ProductPrice price4 = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .build();
        testedPriceRepository.save(price4);

        List<ProductPrice> result = testedPriceRepository.findByProductAndParsingDateIsAfterOrderByParsingDateDesc(
                mockedProduct,
                LocalDate.of(2023, 10, 2).atStartOfDay()
        );

        assertThat(
                result,
                contains(price4, price3)
        );
    }
}