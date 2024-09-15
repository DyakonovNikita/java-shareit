package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponseDto toCommentResponseDto(Comment comment);

    Comment toComment(CommentResponseDto commentResponseDto);

    CommentRequestDto toCommentRequestDto(Comment comment);

    Comment toComment(CommentRequestDto commentRequestDto);
}
