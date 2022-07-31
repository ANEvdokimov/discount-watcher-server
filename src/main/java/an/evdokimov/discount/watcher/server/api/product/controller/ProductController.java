package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Addition a new product to the current user. The product will be parsed from a shop cite.
     *
     * @param newProduct information about the added product.
     * @return an actual product information.
     * @throws ServerException any errors during adding the product.
     */
    @PutMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse addProduct(Authentication authentication,
                                      @Valid @RequestBody NewProductRequest newProduct) throws ServerException {
        log.info("Adding new product {} to user {}", newProduct.toString(),
                ((User) authentication.getPrincipal()).getLogin());
        return productService.addProduct((User) authentication.getPrincipal(), newProduct);
    }

    /**
     * Getting all current user's products.
     *
     * @param authentication      information about a current user.
     * @param withPriceHistory    flag - return product with whole history of changing price [true]
     *                            or only with an actual price [false].
     * @param onlyActive          flag - return products only with a valid promotion.
     * @param shopId              An id of the shop where the products are sold.
     * @param monitorAvailability flag - return products with monitor availability
     * @param monitorDiscount     flag - return products with monitor existence discount
     * @param monitorPriceChanges flag - return products with monitor price decrease
     * @return a list of user's products.
     */
    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ProductResponse> getUserProducts(Authentication authentication,
                                                       @RequestHeader("with-price-history") boolean withPriceHistory,
                                                       @RequestHeader("only-active") boolean onlyActive,
                                                       @RequestHeader("shop-id") @Nullable Long shopId,
                                                       @RequestHeader("monitor-availability") boolean monitorAvailability,
                                                       @RequestHeader("monitor-discount") boolean monitorDiscount,
                                                       @RequestHeader("monitor-price-changes") boolean monitorPriceChanges
    ) throws ServerException {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Getting products. user={}, price_history={}, only-active={}, shopId={}, monitorAvailability={}," +
                        " monitorDiscount={}, monitorPriceChanges={}",
                currentUser.getLogin(), withPriceHistory, onlyActive, shopId,
                monitorAvailability, monitorDiscount, monitorPriceChanges);

        if (shopId != null) {
            return productService.getUserProductsInShop(currentUser, shopId, withPriceHistory, onlyActive,
                    monitorAvailability, monitorDiscount, monitorPriceChanges);
        } else {
            return productService.getUserProducts(currentUser, withPriceHistory, onlyActive, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        }
    }

    /**
     * Getting a product by product id.
     *
     * @param id               a product id.
     * @param withPriceHistory flag - return product with whole history of changing price [true]
     *                         or only with an actual price [false].
     * @return Information about the product.
     * @throws ServerException any errors during getting the product.
     */
    @GetMapping(value = "/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(@PathVariable Long id,
                                      @RequestHeader(name = "with-price-history") boolean withPriceHistory)
            throws ServerException {
        log.info("Getting product by id={}, price_history={}", id, withPriceHistory);
        return productService.getProduct(id, withPriceHistory);
    }
}
