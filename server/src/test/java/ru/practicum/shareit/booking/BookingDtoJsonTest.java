package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldSerializeDateTimeInIsoFormat() throws Exception {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.of(2026, 1, 1, 10, 0))
                .end(LocalDateTime.of(2026, 1, 2, 10, 0))
                .itemId(1L)
                .build();

        assertThat(json.write(dto)).hasJsonPathStringValue("@.start");
        assertThat(json.write(dto)).extractingJsonPathStringValue("@.start")
                .isEqualTo("2026-01-01T10:00:00");
    }

    @Test
    void shouldDeserializeDateTime() throws Exception {
        String content = "{\"start\":\"2026-01-01T10:00:00\",\"end\":\"2026-01-02T10:00:00\",\"itemId\":1}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(dto.getItemId()).isEqualTo(1L);
    }
}
