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
    public ProductResponse addProduct(@Valid @RequestBody NewProductRequest newProduct) throws ServerException {
        log.info("Adding new product: {}", newProduct.toString());
        return productService.addProduct(newProduct);
    }

    /**
     * Getting all current user's products.
     *
     * @param authentication   information about a current user.
     * @param withPriceHistory flag - return product with whole history of changing price [true]
     *                         or only with an actual price [false].
     * @param onlyActive       flag - return only active products.
     * @param shopId           An id of the shop where the products are sold.
     * @return a list of user's products.
     */
    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ProductResponse> getUserProducts(Authentication authentication,
                                                       @RequestHeader(name = "with-price-history")
                                                               boolean withPriceHistory,
                                                       @RequestHeader(name = "only-active")
                                                               boolean onlyActive,
                                                       @RequestHeader(name = "shop-id") @Nullable
                                                               Long shopId) throws ServerException {
        log.info("Getting products. user={}, price_history={}, only-active={}, shopId={}",
                ((User) authentication.getPrincipal()).getLogin(), withPriceHistory, onlyActive, shopId);

        if (shopId != null) {
            return productService.getUserProductsInShop((User) authentication.getPrincipal(), shopId, withPriceHistory,
                    onlyActive);
        } else {
            return productService.getUserProducts((User) authentication.getPrincipal(), withPriceHistory, onlyActive);
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
