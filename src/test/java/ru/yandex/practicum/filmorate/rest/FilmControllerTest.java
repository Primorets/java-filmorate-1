package ru.yandex.practicum.filmorate.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    void shouldCreateFilmWithEmptyName() {
        Film film = new Film(1, "", "good film",
                LocalDate.of(2000, 10, 11), 60);

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.validateFilm(film);
                    }
                });
        assertEquals("invalid name", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithOver200SimbolDescription() {
        String simbols = "The film is set in Middle-earth – a land where such “goodly” races as hobbits, elves," +
                " dwarfs and men live. Since the ancient times they have warred with orcs, goblins and trolls. " +
                "At the beginning of the film we learn about the One Ring – a powerful weapon created by the Dark " +
                "Lord Sauron – which was occasionally found by Bilbo Baggins, the hobbit.\n" +
                "\n" +
                "On his 111th birthday Bilbo had a great party, after which he suddenly departed and left his young " +
                "cousin Frodo all his belongings, including the magic ring.\n" +
                "\n" +
                "A few years later Gandalf, the wizard, visits Frodo to tell him the truth about the ring. " +
                "A short time after that Frodo and three of his friends leave Shire. They are followed by the " +
                "Black Riders, who are searching for Frodo and the Ring. On their way they meet a new friend Aragorn.";
        Film film = new Film(1, "film1", simbols,
                LocalDate.of(2000, 10, 11), 60);

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.validateFilm(film);
                    }
                });
        assertEquals("Описание больше 200 символов.", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithIncorrectReleaseDate() {
        Film film = new Film(1, "film1", "good film",
                LocalDate.of(1000, 10, 11), 60);

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.validateFilm(film);
                    }
                });
        assertEquals("invalid releaseDate", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithIncorrectDuration() {
        Film film = new Film(1, "film1", "good film",
                LocalDate.of(2000, 10, 11), -60);

        FilmsAndUsersValidationException exception = assertThrows(FilmsAndUsersValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.validateFilm(film);
                    }
                });
        assertEquals("invalid duration", exception.getMessage());
    }
}
