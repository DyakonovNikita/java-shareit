package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CommentRequestDto {
    Long id;
    String text;
    String authorName;
    Long itemId;
}
