package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> search(String text);

    void deleteById(Long id);
}
