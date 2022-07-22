package an.evdokimov.discount.watcher.server.parser.downloader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class PageDownloader {
    @Value("${application.parser.lenta-user-agent-header}")
    private String lentaUserAgentHeader;

    @Value("${application.parser.lenta-referer-header}")
    private String lentaRefererHeader;

    public Document downloadPage(URL url, String cookies) throws PageDownloaderException {
        try {
            Connection connection = Jsoup.connect(url.toString());
            if (cookies != null) {
                connection.header("Cookie", cookies);
            }
            connection.header("User-Agent", lentaUserAgentHeader);
            connection.header("Referer", lentaRefererHeader);
            return connection.get();
        } catch (IOException e) {
            throw new PageDownloaderException("Page load error", e);
        }
    }
}
