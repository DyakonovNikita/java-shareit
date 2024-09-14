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
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemResponseDto lastBooking;
    private BookingItemResponseDto nextBooking;
    private Long requestId;
    private List<CommentResponseDto> comments;
}
