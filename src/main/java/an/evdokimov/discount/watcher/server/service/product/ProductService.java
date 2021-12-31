package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.parser.Parser;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ParserFactory parserFactory;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserProductRepository userProductRepository;
    private final ModelMapper mapper;

    public ProductService(ParserFactory parserFactory, ProductRepository productRepository,
                          ShopRepository shopRepository, UserProductRepository userProductRepository,
                          ModelMapper mapper) {
        this.parserFactory = parserFactory;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.userProductRepository = userProductRepository;
        this.mapper = mapper;
    }

    public ProductResponse getProduct(Long id) throws ServerException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServerException(ServerErrorCode.PRODUCT_NOT_FOUND));

        return mapper.map(product, product.getDtoClass());
    }

    public Collection<ProductResponse> getUserProducts(User user) {
        Collection<UserProduct> userProducts = userProductRepository.findByUser(user);
        return userProducts.stream()
                .map(UserProduct::getProduct)
                .map(product -> mapper.map(product, product.getDtoClass()))
                .collect(Collectors.toList());
    }

    public ProductResponse addProduct(NewProductRequest newProduct) throws ServerException {
        Shop shop = shopRepository.findById(newProduct.getShopId())
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        Parser parser;
        try {
            parser = parserFactory.getParser(newProduct.getUrl());
        } catch (ParserFactoryException e) {
            throw new ServerException(ServerErrorCode.UNSUPPORTED_SHOP, e);
        }

        Product parsedProduct;
        try {
            parsedProduct = parser.parse(newProduct.getUrl(), shop);
        } catch (PageDownloaderException e) {
            throw new ServerException(ServerErrorCode.PAGE_DOWNLOAD_ERROR, e);
        } catch (ParserException e) {
            throw new ServerException(ServerErrorCode.PARSE_PAGE_ERROR, e);
        }

        productRepository.save(parsedProduct);

        return mapper.map(parsedProduct, parsedProduct.getDtoClass());
    }
}
