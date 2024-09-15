package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto getById(Long userId, Long bookingId);

    BookingDto updateStatus(Long bookingId, Long userId, Boolean isApproved);

    List<BookingDto> getBookingsByOwner(Long ownerId, String bookingState);

    List<BookingDto> getBookingsByBooker(Long bookerId, String bookingState);
}
