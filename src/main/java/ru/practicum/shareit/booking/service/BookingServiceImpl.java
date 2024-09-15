package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto bookingRequestDto) {

        checkValidDateAndTime(bookingRequestDto.getStart(), bookingRequestDto.getEnd());
        Item item = getItemByIdIfExists(bookingRequestDto.getItemId());
        checkIsItemAvailable(item);
        User owner = getUserByIdIfExists(userId);
        checkAccessForOwnerNotAllowed(item, userId);
        Booking booking = bookingMapper.toBooking(bookingRequestDto);
        booking.setBooker(owner);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return bookingMapper.toBookingDto(bookingJpaRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = getBookingByIdIfExists(bookingId);
        checkAccessAllowedOnlyForOwnerOrBooker(booking, userId);

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean isApprovedBookingStatus) {
        checkIfUserExists(userId);
        Booking booking = getBookingByIdIfExists(bookingId);
        checkAccessAllowedOnlyForOwner(booking.getItem(), userId);
        checkStatusIsWaiting(booking);

        Status status = resolveStatus(isApprovedBookingStatus);
        Booking updated = booking.toBuilder().status(status).build();
        bookingJpaRepository.save(updated);

        return bookingMapper.toBookingDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Long ownerId, String state) {

        checkIfUserExists(ownerId);
        State validState = findBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> listByOwner;

        switch (validState) {
            case ALL:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                listByOwner = bookingJpaRepository
                        .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, now, now);
                break;
            case PAST:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId, now);
                break;
            case FUTURE:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId, now);
                break;
            case REJECTED:
                List<Status> notApprovedStatus = List.of(Status.REJECTED, Status.CANCELED);
                listByOwner = bookingJpaRepository
                        .findAllByItem_Owner_IdAndStatusInOrderByStartDesc(ownerId, notApprovedStatus);
                break;
            case WAITING:
                listByOwner = bookingJpaRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId,
                                Status.valueOf("WAITING"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return listByOwner.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByBooker(Long bookerId, String state) {

        checkIfUserExists(bookerId);
        State validState = findBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> listByBooker;

        switch (validState) {
            case ALL:
                listByBooker = bookingJpaRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, now, now);
                break;
            case PAST:
                listByBooker = bookingJpaRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, now);
                break;
            case FUTURE:
                listByBooker = bookingJpaRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, now);
                break;
            case REJECTED:
                List<Status> notApprovedStatus = List.of(Status.REJECTED, Status.CANCELED);
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStatusInOrderByStartDesc(bookerId, notApprovedStatus);
                break;
            case WAITING:
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.valueOf("WAITING"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return listByBooker.stream()
                .map(bookingMapper::toBookingDto)
                .toList();

    }

    private Status resolveStatus(Boolean isApproved) {
        return isApproved.equals(true) ? Status.APPROVED : Status.REJECTED;
    }

    private void checkValidDateAndTime(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end) || start.isAfter(end)) {
            throw new DateTimeException("Неверные даты начала и/или конца бронирования");
        }
    }

    private Item getItemByIdIfExists(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Не найден Item с itemId = %d", itemId)));
    }


    private Booking getBookingByIdIfExists(Long bookingId) {
        return bookingJpaRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден Booking для bookingId =  %d", bookingId)));
    }

    private User getUserByIdIfExists(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Пользователя с id %d не существует", userId)));
    }


    private void checkIfUserExists(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("Не найден User с userId: %d", userId));
        }
    }

    private void checkAccessForOwnerNotAllowed(Item item, Long userId) {
        if (isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    "Item не найден, owner не может бронировать свой Item");
        }
    }

    private void checkAccessAllowedOnlyForOwner(Item item, Long userId) {

        if (!isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    (String.format("Операция доступна только владельцу Item %s :", item)));
        }
    }

    private void checkAccessAllowedOnlyForOwnerOrBooker(Booking booking, Long userId) {
        if (!isOwner(booking.getItem(), userId) && !isBooker(booking, userId)) {
            throw new AccessIsNotAllowedException(
                    (String.format("Получить информацию о Booking для bookingId = %d. "
                            + "может только владелец Booking, либо владелец Item", booking.getId())));
        }
    }

    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private boolean isBooker(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId);
    }

    private State findBookingState(String bookingState) {
        try {
            return State.valueOf(bookingState);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkIsItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new UnavailableItemException("В настоящий момент вещь недоступна для бронирования.");
        }
    }

    private void checkStatusIsWaiting(Booking booking) {
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new UnavailableItemException(String.format("Вы не можете изменить ранее подтвержденный статус %s",
                    booking.getStatus()));
        }
    }
}

