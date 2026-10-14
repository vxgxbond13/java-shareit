package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_shouldReturn201() throws Exception {
        ItemDto dto = ItemDto.builder().name("Дрель").description("Мощная").available(true).build();
        ItemDto response = ItemDto.builder().id(1L).name("Дрель").description("Мощная").available(true).build();
        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        ItemDto dto = ItemDto.builder().name("Новое").build();
        ItemDto response = ItemDto.builder().id(1L).name("Новое").build();
        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое"));
    }

    @Test
    void addComment_shouldReturn200() throws Exception {
        CommentDto dto = CommentDto.builder().text("Отлично!").build();
        CommentDto response = CommentDto.builder().id(1L).text("Отлично!").authorName("Иван").created(LocalDateTime.now()).build();
        when(itemService.addComment(eq(2L), eq(1L), any(CommentDto.class))).thenReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отлично!"))
                .andExpect(jsonPath("$.authorName").value("Иван"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        ItemDetailsDto response = ItemDetailsDto.builder()
                .id(1L).name("Дрель").description("Мощная").available(true)
                .comments(List.of())
                .build();
        when(itemService.getById(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByOwner_shouldReturn200() throws Exception {
        when(itemService.getByOwner(1L)).thenReturn(List.of(
                ItemWithBookingsDto.builder().id(1L).name("Дрель").build()
        ));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_shouldReturn200() throws Exception {
        when(itemService.search("дрел")).thenReturn(List.of(
                ItemDto.builder().id(1L).name("Дрель").build()
        ));

        mockMvc.perform(get("/items/search").param("text", "дрел"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
