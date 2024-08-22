package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody final ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@RequestBody final ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable final long itemId) {
        return new ResponseEntity<>(itemService.updateItem(itemDto, userId, itemId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable final long itemId) {
        return new ResponseEntity<>(itemService.getItem(itemId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItem(@RequestParam(value = "text") final String text) {
        return new ResponseEntity<>(itemService.getAllItemsByText(text), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return new ResponseEntity<>(itemService.getAllItemsByUserId(userId), HttpStatus.OK);
    }

}
