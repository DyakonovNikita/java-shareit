package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto getUser(long id);

    void createUser(UserDto userDto);

    void updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);
}
