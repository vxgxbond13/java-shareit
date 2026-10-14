package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) return null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto dto) {
        if (dto == null) return null;
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public static ItemDetailsDto toDetailsDto(Item item,
                                              LocalDateTime last,
                                              LocalDateTime next,
                                              List<Comment> comments) {
        if (item == null) return null;
        return ItemDetailsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList()))
                .build();
    }

    public static ItemWithBookingsDto toWithBookingsDto(Item item,
                                                        java.time.LocalDateTime last,
                                                        java.time.LocalDateTime next,
                                                        List<Comment> comments) {
        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList()))
                .build();
    }

    public static CommentDto toCommentDto(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .text(c.getText())
                .authorName(c.getAuthor().getName())
                .created(c.getCreated())
                .build();
    }
}
