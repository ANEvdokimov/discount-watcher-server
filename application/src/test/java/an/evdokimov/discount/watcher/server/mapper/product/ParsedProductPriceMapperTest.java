package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedLentaProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ParsedProductPriceMapperImpl.class)
class ParsedProductPriceMapperTest {
    @Autowired
    private ParsedProductPriceMapper testedMapper;

    @Test
    @DisplayName("update ProductPrice using ParsedProductPrice with all fields")
    void updateNotNullFieldsToProductPrice_withDiscount_success() {
        ParsedProductPrice testedInput = ParsedProductPrice.builder()
                .id(666L)
                .price(BigDecimal.TEN)
                .priceWithDiscount(BigDecimal.TWO)
                .discount(80.0)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        ProductPrice testedOutput = ProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        ProductPrice expectedOutput = ProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(BigDecimal.TEN)
                .priceWithDiscount(BigDecimal.TWO)
                .discount(80.0)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        testedMapper.updateNotNullFieldsToProductPrice(testedInput, testedOutput);

        assertEquals(expectedOutput, testedOutput);
    }

    @Test
    @DisplayName("update ProductPrice using ParsedProductPrice with null discount")
    void updateNotNullFieldsToProductPrice_withoutDiscount_success() {
        ParsedProductPrice testedInput = ParsedProductPrice.builder()
                .id(666L)
                .price(BigDecimal.TEN)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        ProductPrice testedOutput = ProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        ProductPrice expectedOutput = ProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(BigDecimal.TEN)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        testedMapper.updateNotNullFieldsToProductPrice(testedInput, testedOutput);

        assertEquals(expectedOutput, testedOutput);
    }

    @Test
    @DisplayName("update LentaProductPrice using ParsedLentaProductPrice with null discount")
    void updateNotNullFieldsToLentaProductPrice_withoutDiscount_success() {
        ParsedLentaProductPrice testedInput = ParsedLentaProductPrice.builder()
                .id(666L)
                .price(BigDecimal.TEN)
                .priceWithDiscount(BigDecimal.TWO)
                .priceWithCard(BigDecimal.TWO)
                .discount(80.0)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        LentaProductPrice testedOutput = LentaProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        LentaProductPrice expectedOutput = LentaProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(BigDecimal.TEN)
                .priceWithDiscount(BigDecimal.TWO)
                .priceWithCard(BigDecimal.TWO)
                .discount(80.0)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        testedMapper.updateNotNullFieldsToLentaProductPrice(testedInput, testedOutput);

        assertEquals(expectedOutput, testedOutput);
    }

    @Test
    @DisplayName("update LentaProductPrice using ParsedLentaProductPrice with all fields")
    void updateNotNullFieldsToLentaProductPrice_withDiscount_success() {
        ParsedLentaProductPrice testedInput = ParsedLentaProductPrice.builder()
                .id(666L)
                .price(BigDecimal.TEN)
                .priceWithCard(BigDecimal.TEN)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        LentaProductPrice testedOutput = LentaProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        LentaProductPrice expectedOutput = LentaProductPrice.builder()
                .id(666L)
                .product(Product.builder().id(111L).build())
                .creationDate(LocalDateTime.parse("2023-12-09T01:00:00"))
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(BigDecimal.TEN)
                .priceWithCard(BigDecimal.TEN)
                .availabilityInformation("a lot")
                .isInStock(true)
                .parsingDate(LocalDateTime.parse("2023-12-10T00:00:00"))
                .build();

        testedMapper.updateNotNullFieldsToLentaProductPrice(testedInput, testedOutput);

        assertEquals(expectedOutput, testedOutput);
    }
}