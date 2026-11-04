package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdvertisementFilterDto {
    private String keyword;
    private String city;
    private String sortBy = "createdDate";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 12;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
