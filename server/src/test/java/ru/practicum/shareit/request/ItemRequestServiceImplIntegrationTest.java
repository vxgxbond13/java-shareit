package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private User owner;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(User.builder().name("Req").email("req@test.ru").build());
        owner = userRepository.save(User.builder().name("Own").email("own@test.ru").build());
    }

    @Test
    void create_shouldSaveRequest() {
        ItemRequestResponseDto created = requestService.create(requestor.getId(),
                ItemRequestDto.builder().description("Нужна дрель").build());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getCreated()).isNotNull();
    }

    @Test
    void getById_shouldReturnRequestWithItems() {
        ItemRequestResponseDto request = requestService.create(requestor.getId(),
                ItemRequestDto.builder().description("Нужна дрель").build());

        itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель").description("Мощная").available(true)
                .requestId(request.getId())
                .build());

        ItemRequestResponseDto fetched = requestService.getById(requestor.getId(), request.getId());

        assertThat(fetched.getItems()).hasSize(1);
        assertThat(fetched.getItems().get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void getAll_shouldNotIncludeUserRequests() {
        requestService.create(requestor.getId(), ItemRequestDto.builder().description("R1").build());
        requestService.create(owner.getId(), ItemRequestDto.builder().description("R2").build());

        List<ItemRequestResponseDto> all = requestService.getAll(requestor.getId());

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getDescription()).isEqualTo("R2");
    }
}
