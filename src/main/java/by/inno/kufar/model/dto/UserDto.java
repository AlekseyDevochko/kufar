package by.inno.kufar.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String city;
    private LocalDateTime registrationDate;
    private int advertisementsCount;
}
