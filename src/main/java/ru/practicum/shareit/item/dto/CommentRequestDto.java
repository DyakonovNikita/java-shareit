package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CommentRequestDto {
    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
}
