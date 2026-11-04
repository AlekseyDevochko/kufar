package by.inno.kufar.model;

import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.model.dto.CartItemDto;
import by.inno.kufar.model.dto.ImageDto;
import by.inno.kufar.model.dto.UserDto;
import by.inno.kufar.model.dto.UserRegistrationDto;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCity(user.getCity());
        dto.setRegistrationDate(user.getRegistrationDate());
        dto.setAdvertisementsCount(
                user.getAdvertismentList() == null ? 0 : user.getAdvertismentList().size()
        );
        return dto;
    }

    public ImageDto toImageDto(Image image) {
        if (image == null) return null;
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setFileName(image.getFileName());
        dto.setContentType(image.getContentType());
        if (image.getData() != null) {
            dto.setBase64Data(Base64.getEncoder().encodeToString(image.getData()));
        }
        dto.setUrl("/images/" + image.getId());
        return dto;
    }

    public AdvertisementDto toAdvertisementDto(Advertisement ad) {
        if (ad == null) return null;
        AdvertisementDto dto = new AdvertisementDto();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setCity(ad.getCity());
        dto.setCreatedDate(ad.getCreatedDate());
        dto.setSellerUsername(ad.getSeller() != null ? ad.getSeller().getUsername() : null);
        if (ad.getSeller() != null) {
            dto.setSellerId(ad.getSeller().getId());
            dto.setSellerName(ad.getSeller().getFullName());
        }
        if (ad.getImages() != null) {
            dto.setImages(ad.getImages().stream()
                    .map(this::toImageDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /** Применение DTO регистрации к сущности (пароль хешируется в сервисе!) */
    public User toUserEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // хеширование выполняется в UserService
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCity(dto.getCity());
        return user;
    }

    public CartItemDto toCartItemDto(CartItem item) {
        if (item == null) return null;
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setAdvertisement(toAdvertisementDto(item.getAdvertisement()));
        dto.setAddedAt(item.getAddedAt());
        return dto;
    }
}
