package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    public UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос. Список всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получен запрос. Список всех пользователей");
        return userService.get(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userService.save(user);
        log.info("Добавлен пользователь: " +
                user.getName() + " ID: " + user.getId() + " эмэйл: " +
                user.getEmail() + " логин: " + user.getLogin() +
                " дата рождения: " + user.getBirthday());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.get(user.getId());
        userService.save(user);
        log.info("Обновлён пользователь: " +
                user.getName() + " ID: " + user.getId() + " эмэйл: " +
                user.getEmail() + " логин: " + user.getLogin() +
                " дата рождения: " + user.getBirthday());
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователю: " + " ID: " + id + " добавлен друг: ID:" + friendId);
        return userService.get(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
        log.info("У пользователю: " + " ID: " + id + " удалён друг: ID:" + friendId);
        return userService.get(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsList(@PathVariable int id) {
        log.info("Запрос списка друзей пользователя ID: " + id);
        return userService.getALlFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getOtherFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос списка общих друзей пользователя с ID: " + id + "и пользователся с ID: " + otherId);
        return userService.getOthersFriends(id, otherId);
    }
}
