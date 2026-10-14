package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.util.RequestHeaders;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto create(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                         @RequestBody ItemRequestDto dto) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getByUser(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        return requestService.getByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                          @PathVariable Long requestId) {
        return requestService.getById(userId, requestId);
    }
}
