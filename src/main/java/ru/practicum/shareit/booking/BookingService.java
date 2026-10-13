package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(Long userId, BookingDto dto);

    BookingResponseDto approve(Long userId, Long bookingId, boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getByUser(Long userId, BookingState state);

    List<BookingResponseDto> getByOwner(Long userId, BookingState state);
}
