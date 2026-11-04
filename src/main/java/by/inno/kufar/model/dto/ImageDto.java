package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private Long id;
    private String fileName;
    private String contentType;
    /** Base64-строка для отображения в Thymeleaf через data-URI */
    private String base64Data;
    /** URL для скачивания/просмотра файла */
    private String url;
}
