package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.database.product.model.*;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
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
                .parsingStatus(ParsingStatus.COMPLETE)
                .build();
        LentaProductPrice savedLentaProductPrice = testedPriceRepository.saveAndFlush(lentaProductPrice);

        assertTrue(testedPriceRepository.findById(savedLentaProductPrice.getId()).get() instanceof LentaProductPrice);
    }

    @Test
    void findById_ProductPrice_instanceOfProductPrice() {
        ProductPrice productPrice = ProductPrice.builder()
                .product(mockedProduct)
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .parsingStatus(ParsingStatus.COMPLETE)
                .build();
        ProductPrice savedLentaProductPrice = testedPriceRepository.saveAndFlush(productPrice);

        assertFalse(testedPriceRepository.findById(savedLentaProductPrice.getId()).get() instanceof LentaProductPrice);
    }
}