package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private AdvertisementDto advertisement;
    private LocalDateTime addedAt;
}
