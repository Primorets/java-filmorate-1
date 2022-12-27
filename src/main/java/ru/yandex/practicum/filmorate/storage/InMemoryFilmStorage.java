package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int generator = 0;
    private Map<Integer, Film> allFilms = new HashMap<>();

    @Override
    public Film get(int filmId) {
        return allFilms.get(filmId);
    }

    @Override
    public Film save(Film film) {
        if (!allFilms.containsKey(film.getId())) {
            film.setId(++generator);
        }
        allFilms.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Film film, User user) {
        film.getFilmsLike().add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.getFilmsLike().remove(user.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(allFilms.values());
    }
}
