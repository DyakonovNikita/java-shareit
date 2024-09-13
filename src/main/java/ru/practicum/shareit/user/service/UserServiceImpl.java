package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.IncorrectEmailException;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(long id) {
        return userMapper.toUserDto(userRepository.findById(id).orElse(null));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validateUser(user);
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User oldUser = userRepository.findById(id).orElse(null);

        if (user.getEmail() != null) {
            validateUser(user);
            oldUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }

        return userMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null) {
            throw new NullPointerException("email = null");
        }

        if (!user.getEmail().contains("@")) {
            throw new IncorrectEmailException("Некорректный email");
        }

        if (userRepository.findAll()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && user.getId() != u.getId())) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует");
        }
    }
}
