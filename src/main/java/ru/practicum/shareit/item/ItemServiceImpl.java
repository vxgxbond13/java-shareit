package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Статус доступности обязателен");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId))
            throw new NotFoundException("Не владелец");
        if (dto.getName() != null && !dto.getName().isBlank()) item.setName(dto.getName());
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDetailsDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Comment> comments = commentRepository.findByItemId(itemId);

        LocalDateTime last = null;
        LocalDateTime next = null;

        // Даты показываем только владельцу
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            last = bookingRepository
                    .findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, now)
                    .map(Booking::getEnd).orElse(null);
            next = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                    .map(Booking::getStart).orElse(null);
        }

        return ItemMapper.toDetailsDto(item, last, next, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getByOwner(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> {
                    LocalDateTime last = bookingRepository
                            .findFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now)
                            .map(Booking::getEnd).orElse(null);
                    LocalDateTime next = bookingRepository
                            .findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), now)
                            .map(Booking::getStart).orElse(null);
                    List<Comment> comments = commentRepository.findByItemId(item.getId());
                    return ItemMapper.toWithBookingsDto(item, last, next, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean rented = bookingRepository.existsCompletedBooking(userId, itemId, LocalDateTime.now());
        if (!rented) throw new ValidationException("Пользователь не арендовал эту вещь");

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
        return ItemMapper.toCommentDto(commentRepository.save(comment));
    }
}
