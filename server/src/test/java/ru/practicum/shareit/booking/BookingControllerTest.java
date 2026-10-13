package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingResponseDto sampleResponse() {
        return BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2026, 1, 1, 10, 0))
                .end(LocalDateTime.of(2026, 1, 2, 10, 0))
                .status("WAITING")
                .item(ItemShortDto.builder().id(1L).name("Дрель").build())
                .booker(UserShortDto.builder().id(2L).build())
                .build();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2026, 1, 1, 10, 0))
                .end(LocalDateTime.of(2026, 1, 2, 10, 0))
                .build();
        when(bookingService.create(eq(2L), any(BookingDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve_shouldReturn200() throws Exception {
        when(bookingService.approve(eq(1L), eq(1L), eq(true))).thenReturn(sampleResponse());

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        when(bookingService.getById(eq(1L), eq(1L))).thenReturn(sampleResponse());

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByUser_shouldReturn200() throws Exception {
        when(bookingService.getByUser(eq(1L), any(BookingState.class)))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByOwner_shouldReturn200() throws Exception {
        when(bookingService.getByOwner(eq(1L), any(BookingState.class)))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}