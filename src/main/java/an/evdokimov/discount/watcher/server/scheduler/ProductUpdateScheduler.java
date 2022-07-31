package an.evdokimov.discount.watcher.server.scheduler;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateScheduler {
    @Value("${application.product-update.min-delay}")
    private Long minDelay;

    @Value("${application.product-update.max-delay}")
    private Long maxDelay;

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Scheduled(cron = "0 0 0,12 * * *", zone = "UTC") // every day at 0:00 and 12:00
    public void updateProduct() {
        Collection<Product> activeProducts = productRepository.findAllTrackedProducts();

        Random random = new Random();

        for (Product product : activeProducts) {
            try {
                Thread.sleep(random.nextLong(minDelay, maxDelay));
                productService.updateProduct(product);
            } catch (ServerException | InterruptedException e) {
                log.warn("Updating product error: ", e);
            }
        }
    }
}
