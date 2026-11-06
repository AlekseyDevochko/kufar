package by.inno.kufar.service;

import by.inno.kufar.model.Advertisement;
import by.inno.kufar.model.CartItem;
import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.User;
import by.inno.kufar.repository.AdvertisementRepository;
import by.inno.kufar.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartRepo;
    @Mock
    private AdvertisementRepository adRepo;
    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private CartService cartService;

    @Test
    void add_ShouldNotAdd_WhenUserIsSeller() {
        User seller = new User();
        seller.setId(1L);

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);

        when(adRepo.findById(10L)).thenReturn(Optional.of(ad));

        cartService.add(seller, 10L);

        verify(cartRepo, never()).save(any(CartItem.class)); // Не должен сохранять
    }

    @Test
    void getTotal_ShouldReturnSumOfPrices() {
        // Arrange
        User user = new User();
        CartItem item1 = new CartItem();
        Advertisement ad1 = new Advertisement(); ad1.setPrice(BigDecimal.valueOf(100));
        item1.setAdvertisement(ad1);

        CartItem item2 = new CartItem();
        Advertisement ad2 = new Advertisement(); ad2.setPrice(BigDecimal.valueOf(250));
        item2.setAdvertisement(ad2);

        when(cartRepo.findByUserOrderByAddedAtDesc(user)).thenReturn(List.of(item1, item2));

        BigDecimal total = cartService.getTotal(user);

        assertEquals(BigDecimal.valueOf(350), total);
    }
}
