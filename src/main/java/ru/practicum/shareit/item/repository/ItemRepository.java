package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item, long userId, long id);

    Item getItem(long id);

    Collection<Item> getAllItemsByUserId(long userId);

    Collection<Item> getAllItemsByText(String text);
}
