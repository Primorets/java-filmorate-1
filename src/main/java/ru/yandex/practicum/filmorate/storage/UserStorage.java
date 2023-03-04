package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User get(int userId);

    List<User> getCommonFriends(int id, int friendId);

    List<User> getAllUsersList();

    User save(User user);

    void deleteUserById(int id);

    void addFriend(User user, User friend);

    void deleteFriends(User user, User friend);

    User update(User user);

    List<User> getOneUserFriendsList(int userId);
}
