package by.inno.kufar.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CardPaymentDto {

    @NotBlank(message = "Введите номер карты")
    @Pattern(regexp = "^(?:\\d{4}\\s?){3}\\d{4}$",
            message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @NotBlank(message = "Введите имя держателя карты")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$",
            message = "Только буквы, пробелы и дефисы")
    private String cardHolder;

    @NotBlank(message = "Введите срок действия")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$",
            message = "Формат MM/YY (например, 08/27)")
    private String expiry;

    @NotBlank(message = "Введите CVV")
    @Pattern(regexp = "^\\d{3}$", message = "CVV — 3 цифры")
    private String cvv;

    @NotNull
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
