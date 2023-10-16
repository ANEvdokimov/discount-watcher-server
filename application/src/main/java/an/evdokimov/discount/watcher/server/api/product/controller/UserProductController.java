package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.UserProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/products/by_user")
@RequiredArgsConstructor
@Slf4j
public class UserProductController {
    private final UserProductService service;

    /**
     * Getting current user's UserProduct by id.
     *
     * @param authentication information about a current user.
     * @param userProductId  ID of the product you are looking for.
     * @return founded UserProduct.
     * @throws ServerException UserProduct not found.
     */
    @GetMapping(value = "/{userProductId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProductResponse getById(Authentication authentication,
                                       @PathVariable Long userProductId) throws ServerException {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Getting user product by id={} for user={}", userProductId, currentUser.getLogin());
        return service.getById(currentUser, userProductId);
    }

    /**
     * Getting all current user's products.
     *
     * @param authentication      information about a current user.
     * @param onlyActive          flag - return products only with a valid promotion.
     * @param shopId              An id of the shop where the products are sold.
     * @param monitorAvailability flag - return products with monitor availability
     * @param monitorDiscount     flag - return products with monitor existence discount
     * @param monitorPriceChanges flag - return products with monitor price decrease
     * @return a list of user's products.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserProductResponse> getUserProducts(Authentication authentication,
                                                           @RequestHeader("only-active") boolean onlyActive,
                                                           @RequestHeader("shop-id") @Nullable Long shopId,
                                                           @RequestHeader("monitor-availability") @Nullable Boolean monitorAvailability,
                                                           @RequestHeader("monitor-discount") @Nullable Boolean monitorDiscount,
                                                           @RequestHeader("monitor-price-changes") @Nullable Boolean monitorPriceChanges
    ) throws ServerException {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Getting products. user={}, only-active={}, shopId={}, monitorAvailability={}," +
                        " monitorDiscount={}, monitorPriceChanges={}",
                currentUser.getLogin(), onlyActive, shopId,
                monitorAvailability, monitorDiscount, monitorPriceChanges);

        if (shopId != null) {
            return service.getUserProductsInShop(currentUser, shopId, onlyActive,
                    monitorAvailability, monitorDiscount, monitorPriceChanges);
        } else {
            return service.getUserProducts(currentUser, onlyActive, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        }
    }

    /**
     * Update UserProduct
     *
     * @param authentication information about a current user.
     * @param userProduct    updated UserProduct.
     */
    @PostMapping
    public void update(Authentication authentication,
                       @Valid @RequestBody UserProductRequest userProduct) throws ServerException {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Updating UserProduct [{}] for user [{}]", userProduct, currentUser.getLogin());
        service.update(currentUser, userProduct);
    }

    /**
     * Delete UserProduct
     *
     * @param authentication information about a current user.
     * @param userProductId  ID of UserProduct to delete.
     */
    @DeleteMapping("/{userProductId}")
    public void delete(Authentication authentication,
                       @PathVariable Long userProductId) throws ServerException {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Deletion UserProduct [id={}] for user [{}]", userProductId, currentUser.getLogin());
        service.delete(currentUser, userProductId);
    }
}
