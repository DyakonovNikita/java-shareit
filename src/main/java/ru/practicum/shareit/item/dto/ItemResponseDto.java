package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingItemResponseDto lastBooking;
    BookingItemResponseDto nextBooking;
    Long requestId;
    List<CommentResponseDto> comments;
}
