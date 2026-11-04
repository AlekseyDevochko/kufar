package by.inno.kufar.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    @NotBlank(message = "Введите логин")
    private String username;

    @NotBlank(message = "Введите пароль")
    private String password;
}