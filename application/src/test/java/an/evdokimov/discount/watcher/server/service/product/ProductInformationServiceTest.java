package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductInformationServiceImpl.class)
class ProductInformationServiceTest {
    @MockBean
    private ProductInformationRepository repository;


    @Autowired
    private ProductInformationService testedService;

    @SneakyThrows
    @Test
    @DisplayName("get info by id")
    void getById_existentInfo_info() {
        ProductInformation testedInformation = ProductInformation.builder()
                .id(666L)
                .build();

        when(repository.findById(testedInformation.getId())).thenReturn(Optional.of(testedInformation));

        ProductInformation actual = testedService.getById(testedInformation.getId());

        assertEquals(testedInformation, actual);
    }

    @Test
    @DisplayName("get nonexistent info by id")
    void getById_nonexistentInfo_ServerException() {
        when(repository.findById(666L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class, () -> testedService.getById(666L));
    }

    @SneakyThrows
    @Test
    @DisplayName("get info by url")
    void getOrCreateByUrl_existentInfo_info() {
        ProductInformation testedInformation = ProductInformation.builder()
                .id(666L)
                .url(new URL("http://test.test/"))
                .build();

        when(repository.findByUrl(testedInformation.getUrl())).thenReturn(Optional.of(testedInformation));

        ProductInformation actual = testedService.getOrCreateByUrl(testedInformation.getUrl());

        assertEquals(testedInformation, actual);
        verify(repository, never()).save(any());
    }

    @SneakyThrows
    @Test
    @DisplayName("create info by url")
    void getOrCreateByUrl_nonexistentInfo_info() {
        ProductInformation testedInformation = ProductInformation.builder()
                .id(666L)
                .url(new URL("http://test.test/"))
                .build();

        when(repository.findByUrl(testedInformation.getUrl())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(testedInformation);

        ProductInformation actual = testedService.getOrCreateByUrl(testedInformation.getUrl());

        assertEquals(testedInformation, actual);
    }
}