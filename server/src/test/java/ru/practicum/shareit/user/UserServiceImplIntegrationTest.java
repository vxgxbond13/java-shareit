package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldSaveUser() {
        UserDto created = userService.create(
                UserDto.builder().name("Иван").email("ivan@test.ru").build()
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void create_shouldThrowOnDuplicateEmail() {
        userService.create(UserDto.builder().name("Иван").email("dup@test.ru").build());

        assertThatThrownBy(() -> userService.create(
                UserDto.builder().name("Пётр").email("dup@test.ru").build()
        )).isInstanceOf(ConflictException.class);
    }
}
