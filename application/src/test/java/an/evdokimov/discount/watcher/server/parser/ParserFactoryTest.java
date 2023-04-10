package an.evdokimov.discount.watcher.server.parser;

import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloader;
import an.evdokimov.discount.watcher.server.parser.lenta.LentaParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ParserFactory.class, LentaParser.class})
class ParserFactoryTest {
    @MockBean
    private Clock clock;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private PageDownloader pageDownloader;

    @Autowired
    private ParserFactory parserFactory;

    @Test
    void getParser_getLentaParser_LentaParser() throws MalformedURLException, ParserFactoryException {
        assertEquals(
                LentaParser.class,
                parserFactory.getParser(new URL("https://lenta.com/some_path")).getClass()
        );
    }

    @Test
    void getParser_getLentaParserByNameInAnotherCase_LentaParser() throws MalformedURLException,
            ParserFactoryException {
        assertEquals(
                LentaParser.class,
                parserFactory.getParser(new URL("https://LENTA.com/some_path")).getClass()
        );
    }

    @Test
    void getParser_wrongName_exception() {
        assertThrows(
                ParserFactoryException.class,
                () -> parserFactory.getParser(new URL("https://wrong_site.com/some_path"))
        );
    }
}