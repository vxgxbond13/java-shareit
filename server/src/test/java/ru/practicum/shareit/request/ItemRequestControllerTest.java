package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private ItemRequestResponseDto sampleResponse() {
        return ItemRequestResponseDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .items(List.of(ItemShortDto.builder().id(1L).name("Дрель").build()))
                .build();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder().description("Нужна дрель").build();
        when(requestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByUser_shouldReturn200() throws Exception {
        when(requestService.getByUser(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(requestService.getAll(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        when(requestService.getById(eq(1L), eq(1L))).thenReturn(sampleResponse());

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"));
    }
}
