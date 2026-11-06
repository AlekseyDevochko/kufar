package by.inno.kufar.service;

import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.UserProfileUpdateDto;
import by.inno.kufar.model.dto.UserRegistrationDto;
import by.inno.kufar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityMapper entityMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setPassword("plainPassword");
        User mappedUser = new User();

        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(entityMapper.toUserEntity(dto)).thenReturn(mappedUser);

        userService.register(dto);

        assertEquals("hashedPassword", dto.getPassword()); // Проверяем, что пароль заменился на хеш
        verify(userRepository, times(1)).save(mappedUser);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenEmailIsTaken() {
        String username = "testuser";
        UserProfileUpdateDto dto = new UserProfileUpdateDto();
        dto.setEmail("new@example.com");

        User user = new User();
        user.setEmail("old@example.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(new User())); // Email занят

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(username, dto));
    }
}