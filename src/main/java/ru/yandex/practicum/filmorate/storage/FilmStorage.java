package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    Film get(int filmId);

    Film save(Film film);

    Film updateFilm(Film film);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getLikeFromUserId0ForFilm(int filmId, int userId);

    List<Film> getAllFilms();

    List<Film> getMostPopularFilms(int countLikes);

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<Mpa> getAllMpa();

    Mpa getMpaById(int mpaId);
}
