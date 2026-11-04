package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AdvertisementDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String city;
    private LocalDateTime createdDate;
    private Long sellerId;
    private String sellerName;
    private List<ImageDto> images;
    private String sellerUsername;
}
