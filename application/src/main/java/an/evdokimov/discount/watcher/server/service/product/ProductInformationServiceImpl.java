package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductInformationServiceImpl implements ProductInformationService {
    private final ProductInformationRepository repository;

    @Override
    public @NotNull ProductInformation getById(@NotNull Long id) throws ServerException {
        return repository.findById(id)
                .orElseThrow(() -> ServerErrorCode.PRODUCT_INFORMATION_NOT_FOUND.getException("id=" + id));
    }

    @Override
    @NotNull
    @Transactional
    public ProductInformation getOrCreateByUrl(@NotNull URL url) {
        return repository.findByUrl(url)
                .orElseGet(() ->
                        repository.save(
                                ProductInformation.builder()
                                        .url(url)
                                        .parsingStatus(ParsingStatus.PROCESSING)
                                        .build()
                        )
                );
    }
}
