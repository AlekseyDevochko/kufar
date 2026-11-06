package by.inno.kufar.service;

import by.inno.kufar.model.dto.AdvertisementCreateDto;
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.Advertisement;
import by.inno.kufar.model.User;
import by.inno.kufar.repository.AdvertisementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private AdvertisementService advertisementService;

    private User seller;
    private AdvertisementCreateDto createDto;
    private Advertisement advertisement;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId(1L);
        seller.setUsername("seller_user");

        createDto = new AdvertisementCreateDto();
        createDto.setTitle("Test Ad");
        createDto.setPrice(BigDecimal.valueOf(100));

        advertisement = new Advertisement();
        advertisement.setId(10L);
        advertisement.setSeller(seller);
        advertisement.setTitle("Test Ad");
    }

    @Test
    void create_ShouldSaveAdvertisement() {
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(advertisement);
        when(mapper.toAdvertisementDto(any(Advertisement.class))).thenReturn(new AdvertisementDto());

        AdvertisementDto result = advertisementService.create(createDto, seller);

        assertNotNull(result);
        verify(advertisementRepository, times(1)).save(any(Advertisement.class));
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotOwner() {
        User hacker = new User();
        hacker.setId(2L);

        when(advertisementRepository.findById(10L)).thenReturn(Optional.of(advertisement));

        assertThrows(SecurityException.class, () -> {
            advertisementService.delete(10L, hacker);
        });

        verify(advertisementRepository, never()).delete(any());
    }
}
