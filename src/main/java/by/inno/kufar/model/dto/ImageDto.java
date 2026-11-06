package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private Long id;
    private String fileName;
    private String contentType;
    private String base64Data;
    private String url;
}
