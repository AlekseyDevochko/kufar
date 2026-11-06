package by.inno.kufar.service;

import by.inno.kufar.model.EntityMapper;
import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.UserProfileUpdateDto;
import by.inno.kufar.model.dto.UserRegistrationDto;
import by.inno.kufar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private UserRepository userRepository;
    private EntityMapper entityMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            EntityMapper entityMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(
            UserRegistrationDto userRegistrationDto
    ) {
        userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        User user = entityMapper.toUserEntity(userRegistrationDto);
        userRepository.save(user);
        return null; // Затычка
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    // UserService.java — добавьте рядом с findByUsername
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));
    }

    @Transactional
    public void updateProfile(String username, UserProfileUpdateDto dto) {
        User user = findByUsername(username);
        if (!user.getEmail().equals(dto.getEmail()) && existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCity(dto.getCity());
    }
}
