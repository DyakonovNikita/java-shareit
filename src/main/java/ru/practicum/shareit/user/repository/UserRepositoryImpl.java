package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers()  {
        return users.values();
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public void createUser(User user) {
        users.put(user.getId(), user);
    }

    public void updateUser(long id, User user) {
        users.put(id, user);
    }

    public void deleteUser(long id) {
        users.remove(id);
    }
}
