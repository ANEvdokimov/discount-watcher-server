package an.evdokimov.discount.watcher.server.parser;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;

import java.net.URL;

public interface Parser {
    String getSupportedUrl();

    Product parse(URL url, Shop shop) throws PageDownloaderException, ParserException;

    Product parse(ProductInformation productInformation, Shop shop) throws PageDownloaderException, ParserException;

    Product parse(Product product) throws ParserException, PageDownloaderException;
}
