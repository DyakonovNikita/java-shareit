package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final CommentJpaRepository commentRepository;
    private final ItemMapper itemMapper;
    private final ItemJpaRepository itemJpaRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;


    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        validateItem(item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long id) {
        Item item = itemMapper.fromItemDto(itemDto);
        Item oldItem = itemJpaRepository.findById(id).orElse(null);
        assert oldItem != null;
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
            return itemMapper.toItemDto(itemRepository.save(oldItem));
        } else {
            throw new NotFoundException("item - не найден");
        }
    }

    @Override
    @Transactional
    public ItemResponseDto getItem(long id, long userId) {
        List<CommentResponseDto> commentsDto = getCommentsByItemId(id);
        Item item = itemJpaRepository.findById(id).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        BookingItemResponseDto lastBooking = null;
        BookingItemResponseDto nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(id, now);
            nextBooking = getNextBooking(id, now);
        }

        ItemResponseDto itemResponseDto = itemMapper.toItemResponseDto(item);

        itemResponseDto.setLastBooking(lastBooking);
        itemResponseDto.setNextBooking(nextBooking);
        itemResponseDto.setComments(commentsDto);

        return itemResponseDto;
    }

    @Override
    public Collection<ItemDto> getAllItemsByUserId(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getAllItemsByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }


    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isEmpty() ||
                item.getDescription() == null || item.getDescription().isEmpty() ||
                item.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации item");
        } else if (item.getOwner() == null) {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {

        Item item = getItemByIdIfExists(itemId);
        User user = getUserByIdIfExists(userId);
        checkAccessForOwnerNotAllowed(item, userId);
        checkAccessToCommentAllowed(userId, itemId);

        Comment comment = commentMapper.toComment(commentRequestDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        CommentResponseDto commentResponseDto = commentMapper.toCommentResponseDto(comment);
        return commentResponseDto.toBuilder().authorName(user.getName()).itemId(item.getId()).build();
    }

    private User getUserByIdIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден User с userId %d", userId)));
    }

    private Item getItemByIdIfExists(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден item для itemId: %d", itemId)));
    }

    private void checkAccessToCommentAllowed(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore(itemId, userId, Status.APPROVED, now);
        if (bookings.isEmpty()) {
            throw new UnavailableItemException("Нет доступа к comments");
        }
    }

    private void checkAccessForOwnerNotAllowed(Item item, Long userId) {
        if (isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    "Для User с userId " + userId + " не может забронировать item " + item);
        }
    }


    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream()
                .map(commentMapper::toCommentResponseDto)
                .toList();
    }

    private BookingItemResponseDto getLastBooking(Long itemId, LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc(itemId,
                        Status.APPROVED, now, now)
                .map(bookingMapper::toBookingItemResponseDto)
                .orElse(null);

    }

    private BookingItemResponseDto getNextBooking(Long itemId, LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart(itemId,
                        Status.APPROVED, now, now)
                .map(bookingMapper::toBookingItemResponseDto)
                .orElse(null);
    }
}
