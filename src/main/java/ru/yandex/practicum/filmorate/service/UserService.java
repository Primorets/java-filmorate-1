package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ListIsEmptyException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    @Autowired
    public UserStorage userStorage;

    public User get(int userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id= " + userId + "not found");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsersList();
    }

    public List<User> getALlFriends(int userId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getAllUsersList().isEmpty() || userStorage.getAllUsersList() == null) {
            throw new ListIsEmptyException("В списке пользователей ещё нет пользователей");
        } else if (userStorage.get(userId).getFriendsId().isEmpty() || userStorage.get(userId).getFriendsId() == null) {
            throw new ListIsEmptyException("У пользователя ещё нет друзей");
        } else {
            Iterator<Integer> friendId = userStorage.get(userId).getFriendsId().iterator();
            while (friendId.hasNext()) {
                friends.add(userStorage.get(friendId.next()));
            }
            return friends;
        }
    }

    public User save(User user) {
        validateUser(user);
        return userStorage.save(user);
    }

    public void addFriend(int userId, int friendId) {
        if (!checkId(userId, friendId)) {
            User user = get(userId);
            User friend = get(friendId);
            userStorage.addFriend(user, friend);
        } else {
            throw new NotFoundException("Пользователь с ID: " + friendId
                    + "уже в друзьях у пользователя с ID: " + userId);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        if (checkId(userId, friendId)) {
            User user = get(userId);
            User friend = get(friendId);
            userStorage.deleteFriends(user, friend);
        } else {
            throw new NotFoundException("Пользователь с ID: " + friendId
                    + "не найден в друзьях у пользователя с ID: " + userId);
        }
    }

    public List<User> getOthersFriends(int userId, int otherId) {
        Set<Integer> firstUserFriends = new HashSet<>(get(userId).getFriendsId());
        Set<Integer> secondUserFriends = get(otherId).getFriendsId();
        firstUserFriends.retainAll(secondUserFriends);
        List<User> otherFriends = new ArrayList<>();
        Iterator<Integer> friendId = firstUserFriends.iterator();
        while (friendId.hasNext()) {
            otherFriends.add(get(friendId.next()));
        }
        return otherFriends;
    }

    public boolean checkId(int userId, int friendId) {
        User firstUser = get(userId);
        return firstUser.getFriendsId().contains(friendId);
    }

    public void validateUser(User user) throws IllegalArgumentException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmsAndUsersValidationException("Не верная дата рождения. Дата не может быть в будущем.");
        }
        if (user.getLogin().isEmpty()) {
            throw new FilmsAndUsersValidationException("Не верный логин. Логин не может быть пустым.");
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new FilmsAndUsersValidationException("Не верный адрес электронной почты." +
                    " Адрес должен содержать символ '@' и не должены быть пустым.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
