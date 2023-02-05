package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode=DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFilmStorageTest {

    @Qualifier("dbFilmStorage")
    @Autowired
    DbFilmStorage filmStorage;

    @Qualifier("dbUserStorage")
    @Autowired
    private DbUserStorage userStorage;

    private Film film1;

    private Film film2;

    private User user1;

    private User user2;

    private final List<Genre> genresList = new ArrayList<>();

    @Test
    void get() {
        assertEquals(2,filmStorage.getAllFilms().size());
    }

    @Test
    void updateFilm() {
        Film filmUpdate = filmStorage.get(1);
        filmUpdate.setName("updatefilm");
        filmStorage.updateFilm(filmUpdate);
        assertEquals("updatefilm", filmStorage.get(1).getName());
    }

    @Test
    void shouldAddLikeAndDeleteLike() {
        filmStorage.addLike(film1,user1);
        filmStorage.addLike(film1,user2);
        filmStorage.addLike(film2,user1);
        assertEquals(1,filmStorage.getMostPopularFilms(1).get(0).getId());
        filmStorage.deleteLike(film1,user1);
        filmStorage.deleteLike(film1,user1);
        assertEquals(2,filmStorage.getMostPopularFilms(1).get(0).getId());
    }

    @Test
    void getAllFilms() {
        assertEquals(2,filmStorage.getAllFilms().size());
    }

    @Test
    void getAllGenres() {
        assertEquals(6,filmStorage.getAllGenres().size());
    }

    @Test
    void getGenreByIdForFilms() {
        assertEquals(2,filmStorage.getGenreByIdForFilms(1).size());
        assertEquals("Триллер",filmStorage.getGenreByIdForFilms(1).get(1).getName());
    }

    @Test
    void getGenreById() {
        assertEquals("Драма",filmStorage.getGenreById(2).getName());
    }

    @Test
    void getAllMpa() {
        assertEquals(5,filmStorage.getAllMpa().size());
    }

    @Test
    void getMpaById() {
        assertEquals("PG-13",filmStorage.getAllMpa().get(2).getName());
    }

    @BeforeEach
    void init(){
        genresList.add(filmStorage.getGenreById(4));
        genresList.add(filmStorage.getGenreById(2));

        film1 = new Film();
        film1.setName("film1");
        film1.setDescription("f1");
        film1.setDuration(60);
        film1.setReleaseDate(LocalDate.of(1999, 2, 22));
        film1.setMpa(filmStorage.getMpaById(3));
        film1.setGenres(genresList) ;

        film2 = new Film();
        film2.setName("film2");
        film2.setDescription("f2");
        film2.setDuration(62);
        film2.setReleaseDate(LocalDate.of(1999, 2, 22));
        film2.setMpa(filmStorage.getMpaById(2));
        film1.setGenres(genresList);

        user1 = new User();
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1999, 2, 22));
        user1.setEmail("email1@email.com");
        user1.setLogin("login1");

        user2 = new User();
        user2.setName("name1");
        user2.setBirthday(LocalDate.of(1999, 2, 22));
        user2.setEmail("email1@email.com");
        user2.setLogin("login1");

        filmStorage.save(film1);
        filmStorage.save(film2);
        userStorage.save(user1);
        userStorage.save(user2);
    }
}