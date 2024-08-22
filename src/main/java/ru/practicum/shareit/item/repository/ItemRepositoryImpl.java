package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        validateItem(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, long userId, long id) {
        Item oldItem = items.get(id);
        if (oldItem.getOwner().getId() == userId) {
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            items.put(id, oldItem);
        } else {
            throw new NotFoundException("item - не найден");
        }
        return oldItem;
    }

    @Override
    public Item getItem(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("item - не найден");
        }
        return item;
    }

    @Override
    public Collection<Item> getAllItemsByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Collection<Item> getAllItemsByText(String text) {
        String lowerText = text.toLowerCase();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getDescription().toLowerCase().contains(lowerText) ||
                        item.getName().toLowerCase().contains(lowerText)))
                .toList();
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isEmpty() ||
            item.getDescription() == null || item.getDescription().isEmpty() ||
            item.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации item");
        }
    }
}
