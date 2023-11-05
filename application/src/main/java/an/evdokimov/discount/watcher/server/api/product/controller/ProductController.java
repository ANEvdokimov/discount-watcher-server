package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @throws ServerException any errors during adding the product.
     */
    @PutMapping(value = "/products/add_by_shop_id", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addProductByShopId(Authentication authentication,
                                   @Valid @RequestBody NewProductRequest newProduct) throws ServerException {
        log.info("Adding new product {} to user {}", newProduct.toString(),
                ((User) authentication.getPrincipal()).getLogin());
        productService.addProduct((User) authentication.getPrincipal(), newProduct);
    }

    /**
     * Addition a new product to the current user. The product will be parsed from a shop cite.
     *
     * @param newProduct information about the added product.
     * @throws ServerException any errors during adding the product.
     */
    @PutMapping(value = "/products/add_by_cookies", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addProductByCookies(Authentication authentication,
                                    @Valid @RequestBody NewProductWithCookiesRequest newProduct) throws ServerException {
        log.info("Adding new product {} to user {}", newProduct.toString(),
                ((User) authentication.getPrincipal()).getLogin());
        productService.addProduct((User) authentication.getPrincipal(), newProduct);
    }

    /**
     * Getting a product by product id.
     *
     * @param id a product id.
     * @return Information about the product.
     * @throws ServerException any errors during getting the product.
     */
    @GetMapping(value = "/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(@PathVariable Long id)
            throws ServerException {
        log.info("Getting product by id={}", id);
        return productService.getProduct(id);
    }

    /**
     * Parse existing product
     *
     * @param id a product id.
     */
    @PostMapping("/product/update/{id}")
    public void updateProduct(@PathVariable Long id) throws ServerException {
        log.info("Update product by id={}", id);
        productService.updateProduct(id);
    }
}
