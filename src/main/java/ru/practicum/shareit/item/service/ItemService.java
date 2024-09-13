package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long id);

    ItemResponseDto getItem(long id, long userId);

    Collection<ItemDto> getAllItemsByUserId(long userId);

    Collection<ItemDto> getAllItemsByText(String text);

    CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long userId, Long itemId);
}
