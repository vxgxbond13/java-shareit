package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        validateEmail(userDto.getEmail(), null);
        User user = UserMapper.toUser(userDto);
        user.setId(null);
        User saved = userRepository.save(user);
        log.info("Создан пользователь: {}", saved);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            validateEmail(userDto.getEmail(), userId);
            existing.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existing.setName(userDto.getName());
        }

        User updated = userRepository.update(existing);
        log.info("Обновлён пользователь: {}", updated);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        userRepository.deleteById(userId);
        log.info("Удалён пользователь с id={}", userId);
    }

    private void validateEmail(String email, Long currentUserId) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        userRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getId().equals(currentUserId)) {
                throw new ConflictException("Пользователь с email=" + email + " уже существует");
            }
        });
    }
}
