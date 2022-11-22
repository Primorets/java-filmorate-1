package ru.yandex.practicum.filmorate.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    UserController userController = new UserController();

    @Test
    void shouldCreateUserWithIncorrectEmailWithoutDog() {
        User user = new User(1, "Imia", LocalDate.of(2000, 10, 11),
                "mailmail.com", "log");

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userController.validateUser(user);
                    }
                });
        assertEquals("invalid email", exception.getMessage());
    }

    @Test
    void shouldCreateUserWithEmptyEmail() {
        User user = new User(1, "Imia", LocalDate.of(2000, 10, 11),
                "", "log");

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userController.validateUser(user);
                    }
                });
        assertEquals("invalid email", exception.getMessage());
    }

    @Test
    void shouldCreateUserWithoutLogin() {
        User user = new User(1, "Imia", LocalDate.of(2000, 10, 11),
                "mail@mail.com", "");

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userController.validateUser(user);
                    }
                });
        assertEquals("invalid login", exception.getMessage());
    }

    @Test
    void shouldCreateUserWithIncorrectBirthday() {
        User user = new User(1, "Imia", LocalDate.of(2030, 10, 11),
                "mail@mail.com", "log");

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userController.validateUser(user);
                    }
                });
        assertEquals("invalid birthday", exception.getMessage());
    }
}