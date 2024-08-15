package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long id);

    ItemDto getItem(long id);

    Collection<ItemDto> getAllItemsByUserId(long userId);

    Collection<ItemDto> getAllItemsByText(String text);
}
