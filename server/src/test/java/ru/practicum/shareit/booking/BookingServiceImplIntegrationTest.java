package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Long itemId;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Owner").email("o@t.ru").build());
        booker = userRepository.save(User.builder().name("Booker").email("b@t.ru").build());
        ItemDto item = itemService.create(owner.getId(),
                ItemDto.builder().name("Дрель").description("Мощная").available(true).build());
        itemId = item.getId();
    }

    @Test
    void create_shouldSaveBookingWithStatusWaiting() {
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingResponseDto created = bookingService.create(booker.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("WAITING");
    }

    @Test
    void approve_shouldChangeStatusToApproved() {
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponseDto created = bookingService.create(booker.getId(), dto);

        BookingResponseDto approved = bookingService.approve(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void getByUser_shouldReturnBookings() {
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.create(booker.getId(), dto);

        List<BookingResponseDto> list = bookingService.getByUser(booker.getId(), BookingState.ALL);

        assertThat(list).hasSize(1);
    }
}
