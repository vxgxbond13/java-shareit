package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(
                User.builder().name("Owner").email("owner@test.ru").build()
        );
    }

    @Test
    void create_shouldSaveItemToDatabase() {
        ItemDto dto = ItemDto.builder()
                .name("Дрель").description("Мощная").available(true)
                .build();

        ItemDto created = itemService.create(owner.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Дрель");
    }

    @Test
    void getByOwner_shouldReturnItems() {
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель").description("Мощная").available(true).build());

        List<ItemWithBookingsDto> items = itemService.getByOwner(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void search_shouldFindByTextInNameAndDescription() {
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель").description("Мощная").available(true).build());
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Молоток").description("Дрель не нужна").available(true).build());

        List<ItemDto> result = itemService.search("дрел");

        assertThat(result).hasSize(2);
    }

    @Test
    void search_shouldReturnOnlyAvailableItems() {
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель").description("Мощная").available(false).build());

        List<ItemDto> result = itemService.search("дрел");

        assertThat(result).isEmpty();
    }
}
