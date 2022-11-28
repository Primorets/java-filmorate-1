package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> allUsers = new HashMap<>();
    private int id = 0;

    @GetMapping()
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        usersList.addAll(allUsers.values());
        log.info("Получен запрос. Список всех пользователей");
        return usersList;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        user.setId(++id);
        validateUser(user);
        allUsers.put(id, user);
        log.info("Добавлен пользователь: " +
                user.getName() + " ID: " + user.getId() + " эмэйл: " +
                user.getEmail() + " логин: " + user.getLogin() +
                " дата рождения: " + user.getBirthday());
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (!allUsers.containsKey(user.getId())) {
            throw new ValidationException("invalid id");
        }
        allUsers.put(user.getId(), user);
        log.info("Обновлён пользователь: " +
                user.getName() + " ID: " + user.getId() + " эмэйл: " +
                user.getEmail() + " логин: " + user.getLogin() +
                " дата рождения: " + user.getBirthday());
        return user;
    }

    protected void validateUser(User user) throws IllegalArgumentException {
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
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
