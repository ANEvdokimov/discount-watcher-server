package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapperImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductPriceServiceImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductPriceServiceImplTest {
    @Autowired
    private ProductPriceServiceImpl testedService;
    @SpyBean
    private ProductPriceMapperImpl productPriceMapper;
    @MockBean
    private ProductPriceRepository priceRepository;
    @MockBean
    private ProductRepository productRepository;

    private Product mockedProduct;

    @SneakyThrows
    @BeforeAll
    void createProduct() {
        City city = City.builder()
                .name("city-17")
                .cyrillicName("city-17")
                .build();
        ShopChain shopChain = ShopChain.builder()
                .name("shop-chain-1")
                .build();
        Shop shop = Shop.builder()
                .shopChain(shopChain)
                .city(city)
                .name("shop-name-1")
                .address("address-1")
                .build();
        ProductInformation information = ProductInformation.builder()
                .parsingStatus(ParsingStatus.COMPLETE)
                .name("product-1")
                .url(new URL("https://url-1.test"))
                .build();
        mockedProduct = Product.builder()
                .id(666L)
                .productInformation(information)
                .shop(shop)
                .build();
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_group_prices() {
        ProductPrice price1 = ProductPrice.builder()
                .id(1L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        ProductPrice price2 = ProductPrice.builder()
                .id(2L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();

        ProductPrice price3 = ProductPrice.builder()
                .id(3L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .creationDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .build();
        ProductPrice price4 = ProductPrice.builder()
                .id(4L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .build();
        when(productRepository.findById(mockedProduct.getId()))
                .thenReturn(Optional.of(mockedProduct));
        when(priceRepository.findByProductOrderByParsingDateDesc(mockedProduct))
                .thenReturn(List.of(price4, price3, price2, price1));

        List<ProductPriceResponse> result = testedService.getPrices(mockedProduct.getId(), true, null);

        assertThat(
                result.stream().map(ProductPriceResponse::getId).toList(),
                contains(price3.getId(), price1.getId())
        );
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_noGroup_allPrices() {
        ProductPrice price1 = ProductPrice.builder()
                .id(1L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        ProductPrice price2 = ProductPrice.builder()
                .id(2L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();

        ProductPrice price3 = ProductPrice.builder()
                .id(3L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .creationDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .build();
        ProductPrice price4 = ProductPrice.builder()
                .id(4L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .build();
        when(productRepository.findById(mockedProduct.getId()))
                .thenReturn(Optional.of(mockedProduct));
        when(priceRepository.findByProductOrderByParsingDateDesc(mockedProduct))
                .thenReturn(List.of(price4, price3, price2, price1));

        List<ProductPriceResponse> result = testedService.getPrices(mockedProduct.getId(), false, null);

        assertThat(
                result.stream().map(ProductPriceResponse::getId).toList(),
                contains(price4.getId(), price3.getId(), price2.getId(), price1.getId())
        );
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_equalActualPrices_prices() {
        ProductPrice price1 = ProductPrice.builder()
                .id(1L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        ProductPrice price2 = ProductPrice.builder()
                .id(2L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();
        when(productRepository.findById(mockedProduct.getId()))
                .thenReturn(Optional.of(mockedProduct));
        when(priceRepository.findByProductOrderByParsingDateDesc(mockedProduct))
                .thenReturn(List.of(price2, price1));

        List<ProductPriceResponse> result = testedService.getPrices(mockedProduct.getId(), true, null);

        assertThat(
                result.stream().map(ProductPriceResponse::getId).toList(),
                contains(price1.getId())
        );
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_noGroupAndStartDate_latestPrices() {
        ProductPrice price1 = ProductPrice.builder()
                .id(1L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        ProductPrice price2 = ProductPrice.builder()
                .id(2L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();

        ProductPrice price3 = ProductPrice.builder()
                .id(3L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .creationDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .build();
        ProductPrice price4 = ProductPrice.builder()
                .id(4L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .build();
        when(productRepository.findById(mockedProduct.getId()))
                .thenReturn(Optional.of(mockedProduct));
        when(priceRepository.findByProductOrderByParsingDateDesc(mockedProduct))
                .thenReturn(List.of(price4, price3, price2, price1));
        when(priceRepository.findByProductAndParsingDateIsAfterOrderByParsingDateDesc(
                mockedProduct, LocalDate.of(2023, 10, 2).atStartOfDay()))
                .thenReturn(List.of(price4, price3));

        List<ProductPriceResponse> result = testedService.getPrices(
                mockedProduct.getId(), false, LocalDate.of(2023, 10, 2));

        assertThat(
                result.stream().map(ProductPriceResponse::getId).toList(),
                contains(price4.getId(), price3.getId())
        );
    }

    @SneakyThrows
    @Test
    void findGroupedByProduct_groupAndStartDate_latestPrices() {
        ProductPrice price1 = ProductPrice.builder()
                .id(1L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .creationDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 1, 0))
                .build();
        ProductPrice price2 = ProductPrice.builder()
                .id(2L)
                .product(mockedProduct)
                .price(BigDecimal.TEN)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();

        ProductPrice price3 = ProductPrice.builder()
                .id(3L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .creationDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 1, 0))
                .build();
        ProductPrice price4 = ProductPrice.builder()
                .id(4L)
                .product(mockedProduct)
                .price(BigDecimal.TWO)
                .priceWithDiscount(null)
                .discount(null)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .creationDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .parsingDate(LocalDateTime.of(2023, 10, 2, 12, 0))
                .build();
        when(productRepository.findById(mockedProduct.getId()))
                .thenReturn(Optional.of(mockedProduct));
        when(priceRepository.findByProductOrderByParsingDateDesc(mockedProduct))
                .thenReturn(List.of(price4, price3, price2, price1));
        when(priceRepository.findByProductAndParsingDateIsAfterOrderByParsingDateDesc(
                mockedProduct, LocalDate.of(2023, 10, 2).atStartOfDay()))
                .thenReturn(List.of(price4, price3));

        List<ProductPriceResponse> result = testedService.getPrices(
                mockedProduct.getId(), true, LocalDate.of(2023, 10, 2));

        assertThat(
                result.stream().map(ProductPriceResponse::getId).toList(),
                contains(price3.getId())
        );
    }
}