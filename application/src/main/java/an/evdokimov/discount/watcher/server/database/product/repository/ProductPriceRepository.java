package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

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
    Optional<ProductPrice> findLastCompletedPriceByProduct(@Param("product") Product product);

    List<ProductPrice> findByProductOrderByParsingDateDesc(Product product);

    default List<ProductPrice> findByProductAndGroup(@Param("product") Product product) {
        List<ProductPrice> allPrices = findByProductOrderByParsingDateDesc(product);

        Stack<ProductPrice> result = new Stack<>();
        for (int i = allPrices.size() - 1; i >= 0; i--) {
            if (result.isEmpty()) {
                result.add(allPrices.get(i));
            } else {
                BigDecimal actualPrice1 = result.peek().getPriceWithDiscount() != null
                        ? result.peek().getPriceWithDiscount() : result.peek().getPrice();
                BigDecimal actualPrice2 = allPrices.get(i).getPriceWithDiscount() != null
                        ? allPrices.get(i).getPriceWithDiscount() : allPrices.get(i).getPrice();

                if (!(Objects.equals(result.peek().getParsingDate().toLocalDate(), allPrices.get(i).getParsingDate().toLocalDate())
                        && Objects.equals(actualPrice1, actualPrice2))) {
                    result.add(allPrices.get(i));
                }
            }
        }

        Collections.reverse(result);
        return result;
    }
}
