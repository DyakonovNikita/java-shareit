package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private long userId;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(long id) {
        return userMapper.toUserDto(userRepository.getUser(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setId(getNewId());
        return userMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public void updateUser(long id, UserDto userDto) {
        userRepository.updateUser(id, userMapper.toUser(userDto));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    private long getNewId() {
        return ++userId;
    }
}
