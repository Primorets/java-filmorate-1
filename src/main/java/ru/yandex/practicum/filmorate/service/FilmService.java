package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ListIsEmptyException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    @Autowired
    public UserStorage userStorage;

    @Autowired
    public FilmStorage filmStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmOnId(int filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("User with id= " + filmId + "not found");
        }
        return filmStorage.get(filmId);
    }

    public Film createFilm(Film film) {
        return filmStorage.save(film);
    }

    public void addLike(int filmId, int userId) {
        if (!checkId(filmId, userId)) {
            User user = userStorage.get(userId);
            Film film = getFilmOnId(filmId);
            filmStorage.addLike(film, user);
        } else {
            throw new NotFoundException("Пользователь с ID: " + userId
                    + "уже поставил лайк фильму с ID: " + filmId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (checkId(filmId, userId)) {
            User user = userStorage.get(userId);
            Film film = getFilmOnId(filmId);
            filmStorage.deleteLike(film, user);
        } else {
            throw new NotFoundException("Пользователь с ID: " + userId
                    + "не ставил лайк фильму с ID: " + filmId);
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> mostPopularFilms = new ArrayList<>();
        if (filmStorage.getAllFilms() == null || filmStorage.getAllFilms().isEmpty()) {
            throw new ListIsEmptyException("В списке нет фильмов");
        } else {
            List<Film> filmSort = new ArrayList<>(filmStorage.getAllFilms());
            filmSort.sort((film1, film2) -> film2.getFilmsLike().size() - film1.getFilmsLike().size());
            Iterator<Film> filmIterator = filmSort.iterator();
            while (filmIterator.hasNext() && mostPopularFilms.size() < count) {
                mostPopularFilms.add(filmIterator.next());
            }
        }
        return mostPopularFilms;
    }

    public boolean checkId(int filmId, int userId) {
        Film film = filmStorage.get(filmId);
        if (userStorage.get(userId) == null) {
            throw new NotFoundException("User with id= " + userId + "not found");
        }
        return film.getFilmsLike().contains(userId);
    }

}
