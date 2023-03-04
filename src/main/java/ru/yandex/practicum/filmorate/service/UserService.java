package ru.yandex.practicum.filmorate.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@NoArgsConstructor
public class UserService {

    public UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        try {
            return userStorage.getOneUserFriendsList(userId);
        } catch (ValidationException validationException) {
            throw new ValidationException("Ошибка в списке друзей.");
        }
    }

    public User save(User user) {
        validateUser(user);
        if (user.getId() != null) {
            return userStorage.update(user);
        }
        return userStorage.save(user);
    }

    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
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
        try {
            User user = get(userId);
            User friend = get(friendId);
            userStorage.deleteFriends(user, friend);
        } catch (Exception exception) {
            throw new NotFoundException("Пользователь с ID: " + friendId
                    + "не найден в друзьях у пользователя с ID: " + userId);
        }
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public boolean checkId(int userId, int friendId) {
        if (userId < 0 || friendId < 0) {
            throw new NotFoundException("ID не существует.");
        }
        User friendUser = get(friendId);
        return userStorage.getOneUserFriendsList(userId).contains(friendUser);
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
