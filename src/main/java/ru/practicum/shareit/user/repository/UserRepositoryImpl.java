package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.exception.IncorrectEmailException;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers()  {
        return users.values();
    }

    public User getUser(long id) throws NotFoundException {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User с id " + id + " не найден");
        }
        return users.get(id);
    }

    public User createUser(User user) {
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(long id, User user) {
        User oldUser = users.get(id);
        user.setId(id);
        if (user.getEmail() != null) {
            validateUser(user);
            oldUser.setEmail(user.getEmail());
        }
        oldUser.setName(user.getName());
        users.put(id, oldUser);
        return oldUser;
    }

    public void deleteUser(long id) {
        users.remove(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null) {
            throw new NullPointerException("email = null");
        }

        if (!user.getEmail().contains("@")) {
            throw new IncorrectEmailException("Некорректный email");
        }

        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && user.getId() != u.getId())) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует");
        }
    }
}