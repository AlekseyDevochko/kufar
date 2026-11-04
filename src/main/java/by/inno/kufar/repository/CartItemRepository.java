package by.inno.kufar.repository;

import by.inno.kufar.model.Advertisement;
import by.inno.kufar.model.CartItem;
import by.inno.kufar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByAddedAtDesc(User user);
    boolean existsByUserAndAdvertisement(User user, Advertisement ad);
    void deleteAllByUser(User user);
}
