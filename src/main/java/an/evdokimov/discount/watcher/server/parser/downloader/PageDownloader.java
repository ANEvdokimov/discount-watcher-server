package an.evdokimov.discount.watcher.server.parser.downloader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class PageDownloader {
    public Document downloadPage(URL url, String cookies) throws PageDownloaderException {
        try {
            Connection connection = Jsoup.connect(url.toString());
            if (cookies != null) {
                connection.header("Cookie", cookies);
            }
            return connection.get();
        } catch (IOException e) {
            throw new PageDownloaderException("Page load error", e);
        }
    }
}
