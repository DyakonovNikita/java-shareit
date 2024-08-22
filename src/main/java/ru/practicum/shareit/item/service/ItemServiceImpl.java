package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    private long id;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User user = userRepository.getUser(userId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        item.setId(getNewId());

        return itemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long id) {
        return itemMapper.toItemDto(itemRepository.updateItem(itemMapper.fromItemDto(itemDto), userId, id));
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(itemRepository.getItem(id));
    }

    @Override
    public Collection<ItemDto> getAllItemsByUserId(long userId) {
        return itemRepository.getAllItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getAllItemsByText(String text) {
        return itemRepository.getAllItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private long getNewId() {
        return ++id;
    }
}
