package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder(toBuilder = true)
@AllArgsConstructor
public class BookingItemResponseDto {
    private Long id;
    private Long bookerId;
}
