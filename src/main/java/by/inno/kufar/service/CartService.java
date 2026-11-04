package by.inno.kufar.service;

import by.inno.kufar.model.Advertisement;
import by.inno.kufar.model.CartItem;
import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.CartItemDto;
import by.inno.kufar.repository.AdvertisementRepository;
import by.inno.kufar.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final AdvertisementRepository adRepo;
    private final EntityMapper mapper;

    public CartService(CartItemRepository cartRepo,
                       AdvertisementRepository adRepo,
                       EntityMapper mapper) {
        this.cartRepo = cartRepo;
        this.adRepo = adRepo;
        this.mapper = mapper;
    }

    @Transactional
    public void add(User user, Long adId) {
        Advertisement ad = adRepo.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));
        if (ad.getSeller().getId().equals(user.getId())) {
            return;   // свои товары не добавляем
        }
        if (cartRepo.existsByUserAndAdvertisement(user, ad)) {
            return;   // уже в корзине
        }
        CartItem item = new CartItem();
        item.setUser(user);
        item.setAdvertisement(ad);
        item.setAddedAt(LocalDateTime.now());
        cartRepo.save(item);
    }

    @Transactional
    public void remove(User user, Long cartItemId) {
        cartRepo.findById(cartItemId).ifPresent(item -> {
            if (item.getUser().getId().equals(user.getId())) {
                cartRepo.delete(item);
            }
        });
    }

    @Transactional
    public void clear(User user) {
        cartRepo.deleteAllByUser(user);
    }

    @Transactional(readOnly = true)
    public List<CartItemDto> getItems(User user) {
        return cartRepo.findByUserOrderByAddedAtDesc(user).stream()
                .map(mapper::toCartItemDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotal(User user) {
        return cartRepo.findByUserOrderByAddedAtDesc(user).stream()
                .map(i -> i.getAdvertisement().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
