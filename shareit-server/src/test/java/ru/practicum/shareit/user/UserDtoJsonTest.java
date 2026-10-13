package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void shouldSerializeAndDeserialize() throws Exception {
        UserDto dto = UserDto.builder().id(1L).name("Иван").email("i@m.ru").build();

        assertThat(json.write(dto)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(dto)).extractingJsonPathStringValue("@.email").isEqualTo("i@m.ru");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = "{\"name\":\"Иван\",\"email\":\"i@m.ru\"}";

        UserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Иван");
        assertThat(dto.getEmail()).isEqualTo("i@m.ru");
    }
}
