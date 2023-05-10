package an.evdokimov.discount.watcher.server.scheduler;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateScheduler {
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Scheduled(cron = "0 0 0,12 * * *", zone = "UTC") // every day at 0:00 and 12:00
    public void updateProducts() {
        log.info("Start updating products");

        Collection<Product> activeProducts = productRepository.findAllTrackedProducts();
        for (Product product : activeProducts) {
            productService.updateProduct(product);
        }
    }
}