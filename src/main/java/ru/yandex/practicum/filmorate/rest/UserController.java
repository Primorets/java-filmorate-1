package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    Map<Integer, User> allUsers = new HashMap<>();
    int id = 0;

    @GetMapping()
    Collection<User> getAllUsers() {
        log.info("Получен запрос. Список всех пользователей");
        return allUsers.values();
    }

    @PostMapping()
    User createUser(@Valid @RequestBody User user) {
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
    User updateUser(@Valid @RequestBody User user) {
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

    void validateUser(User user) throws IllegalArgumentException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmsAndUsersValidationException("invalid birthday");
        }
        if (user.getLogin().isEmpty()) {
            throw new FilmsAndUsersValidationException("invalid login");
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new FilmsAndUsersValidationException("invalid email");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
