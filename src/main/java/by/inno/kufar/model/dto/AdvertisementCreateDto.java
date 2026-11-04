package by.inno.kufar.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AdvertisementCreateDto {

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 3, max = 100, message = "Заголовок должен быть от 3 до 100 символов")
    private String title;

    @NotBlank(message = "Описание обязательно")
    @Size(min = 10, max = 2000, message = "Описание должно быть от 10 до 2000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Digits(integer = 10, fraction = 2, message = "Некорректный формат цены")
    private BigDecimal price;

    @NotBlank(message = "Город обязателен")
    @Size(max = 100)
    private String city;

    @Size(max = 10, message = "Можно загрузить не более 10 изображений")
    private List<MultipartFile> images;
}
