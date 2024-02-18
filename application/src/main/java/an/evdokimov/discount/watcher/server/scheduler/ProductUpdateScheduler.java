package an.evdokimov.discount.watcher.server.scheduler;

import an.evdokimov.discount.watcher.server.api.product.maintenance.ProductMaintenance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateScheduler {
    private final ProductMaintenance productMaintenance;

    @Scheduled(cron = "#{productSchedulerProperties.update}", zone = "UTC")
    public void updateProducts() {
        log.info("Start updating products");
        productMaintenance.updateTrackedProducts();
    }
}
