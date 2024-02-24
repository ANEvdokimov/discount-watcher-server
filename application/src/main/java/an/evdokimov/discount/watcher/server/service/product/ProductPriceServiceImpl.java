package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements ProductPriceService {
    private final ProductRepository productRepository;
    private final ProductPriceRepository priceRepository;
    private final ProductPriceMapper mapper;

    @Override
    @NotNull
    public Optional<ProductPrice> findLastCompletedPriceByProduct(Product product) {
        log.trace("search last completed ProductPrice for product [productId={}]", product.getId());
        return priceRepository.findLastCompletedPriceByProduct(product);
    }

    @Override
    @NotNull
    public ProductPrice getById(@NotNull Long id) throws ServerException {
        return priceRepository.findById(id)
                .orElseThrow(() -> ServerErrorCode.PRODUCT_PRICE_NOT_FOUND.getException("id=" + id));
    }

    @Override
    @NotNull
    public List<ProductPriceResponse> getPrices(@NotNull Long productId, boolean group, @Nullable LocalDate startDate)
            throws ServerException {
        log.trace("getting prices [productId={}, group={}, startDate={}]", productId, group, startDate);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ServerErrorCode.PRODUCT_NOT_FOUND.getException("id=" + productId));

        List<ProductPrice> prices;
        if (startDate == null) {
            prices = priceRepository.findByProductOrderByParsingDateDesc(product);
        } else {
            prices = priceRepository
                    .findByProductAndParsingDateIsAfterOrderByParsingDateDesc(product, startDate.atStartOfDay());
        }

        if (group) {
            prices = groupByDateAndPrice(prices);
        }

        return prices.stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void savePrice(@NotNull ProductPrice price) {
        log.trace("saving price [{}]", price);

        priceRepository.save(price);
    }

    private List<ProductPrice> groupByDateAndPrice(@NotNull List<ProductPrice> prices) {
        Stack<ProductPrice> result = new Stack<>();
        for (int i = prices.size() - 1; i >= 0; i--) {
            if (result.isEmpty()) {
                result.add(prices.get(i));
            } else {
                BigDecimal actualPrice1 = result.peek().getPriceWithDiscount() != null
                        ? result.peek().getPriceWithDiscount() : result.peek().getPrice();
                BigDecimal actualPrice2 = prices.get(i).getPriceWithDiscount() != null
                        ? prices.get(i).getPriceWithDiscount() : prices.get(i).getPrice();

                if (!(Objects.equals(result.peek().getParsingDate().toLocalDate(),
                        prices.get(i).getParsingDate().toLocalDate())
                        && Objects.equals(actualPrice1, actualPrice2))) {
                    result.add(prices.get(i));
                }
            }
        }
        Collections.reverse(result);
        return result;
    }
}
