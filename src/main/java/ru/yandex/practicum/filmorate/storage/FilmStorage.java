package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    Film get(int filmId);

    Film save(Film film);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getAllFilms();
}
