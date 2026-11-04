package by.inno.kufar.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDto {

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный телефон")
    private String phoneNumber;

    @NotBlank(message = "Город обязателен")
    @Size(max = 100)
    private String city;
}
