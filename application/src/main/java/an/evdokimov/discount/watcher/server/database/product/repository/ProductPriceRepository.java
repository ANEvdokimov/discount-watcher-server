package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    @Query("""
            select price from ProductPrice price
            where price.product = :product
            and price.parsingStatus = 'COMPLETE'
            and price.price is not null
            and price.priceChange != 'UNDEFINED'
            order by price.parsingDate desc
            limit 1
            """)
    Optional<ProductPrice> findLastPriceByProduct(@Param("product") Product product);
}
