package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getAllUsers();

    User getUser(long id);

    void createUser(User user);

    void updateUser(long id, User user);

    void deleteUser(long id);
}
