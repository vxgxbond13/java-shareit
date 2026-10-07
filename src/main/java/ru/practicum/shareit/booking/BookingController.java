package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.util.RequestHeaders;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                     @Valid @RequestBody BookingDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getByUser(
            @RequestHeader(RequestHeaders.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(
            @RequestHeader(RequestHeaders.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getByOwner(userId, state);
    }
}
