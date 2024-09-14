package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody final ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody final ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable final long itemId) {
        return new ResponseEntity<>(itemService.updateItem(itemDto, userId, itemId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable final long itemId) {
        return new ResponseEntity<>(itemService.getItem(itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItem(@RequestParam(value = "text") final String text) {
        return new ResponseEntity<>(itemService.getAllItemsByText(text), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return new ResponseEntity<>(itemService.getAllItemsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestBody CommentRequestDto commentRequestDto,
                                                         @PathVariable long itemId) {
        return new ResponseEntity<>(itemService.addComment(commentRequestDto, userId, itemId), HttpStatus.OK);
    }
}
