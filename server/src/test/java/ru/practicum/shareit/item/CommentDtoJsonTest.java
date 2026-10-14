package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void shouldSerializeCreatedDateTime() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Отлично!")
                .authorName("Иван")
                .created(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();

        assertThat(json.write(dto)).extractingJsonPathStringValue("@.created")
                .isEqualTo("2026-01-01T10:00:00");
    }

    @Test
    void shouldDeserializeText() throws Exception {
        String content = "{\"text\":\"Отлично!\"}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Отлично!");
    }
}
