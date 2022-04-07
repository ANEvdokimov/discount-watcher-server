package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    @PutMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse addProduct(@Valid @RequestBody NewProductRequest newProduct) throws ServerException {
        log.info("Adding new product: {}", newProduct.toString());
        return productService.addProduct(newProduct);
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ProductResponse> getUserProducts(Authentication authentication,
                                                       @RequestHeader(name = "with_price_history")
                                                               boolean withPriceHistory) {
        log.info("Getting products. user={}, price_history={}",
                ((User) authentication.getPrincipal()).getLogin(), withPriceHistory);
        return productService.getUserProducts((User) authentication.getPrincipal(), withPriceHistory);
    }

    @GetMapping(value = "/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(@PathVariable Long id,
                                      @RequestHeader(name = "with_price_history") boolean withPriceHistory)
            throws ServerException {
        log.info("Getting product by id={}, price_history={}", id, withPriceHistory);
        return productService.getProduct(id, withPriceHistory);
    }
}
