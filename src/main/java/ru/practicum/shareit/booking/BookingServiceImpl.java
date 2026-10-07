package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingDto dto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) throw new ValidationException("Вещь недоступна");
        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart()))
            throw new ValidationException("Некорректные даты");

        Booking booking = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId))
            throw new ValidationException("Не владелец вещи");
        if (booking.getStatus() != BookingStatus.WAITING)
            throw new ValidationException("Статус уже установлен");
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isBooker && !isOwner) throw new NotFoundException("Нет доступа");
        return toResponse(booking);
    }

    @Override
    public List<BookingResponseDto> getByUser(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> list = state.findForBooker(bookingRepository, userId, LocalDateTime.now());
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> list = state.findForOwner(bookingRepository, userId, LocalDateTime.now());
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BookingResponseDto toResponse(Booking b) {
        return BookingResponseDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus().name())
                .item(ItemShortDto.builder().id(b.getItem().getId()).name(b.getItem().getName()).build())
                .booker(UserShortDto.builder().id(b.getBooker().getId()).build())
                .build();
    }
}
