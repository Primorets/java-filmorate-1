package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int generator = 0;
    private Map<Integer, Film> allFilms = new HashMap<>();

    private Map<Integer, Genre> allGenre = new HashMap<>();

    private Map<Integer, Mpa> allMpa = new HashMap<>();

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
    public Film updateFilm(Film film) {
        return null;
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
    public List<Film> getLikeFromUserId0ForFilm(int filmId, int userId) {
        List<Film> userLikes = new ArrayList<>();
        for (Film film : allFilms.values()) {
            if (film.getFilmsLike().contains(userId)) {
                userLikes.add(get(filmId));
            }
        }
        return userLikes;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(allFilms.values());
    }

    @Override
    public List<Film> getMostPopularFilms(int countLikes) {
        List<Film> mostPopularFilms = new ArrayList<>();
        List<Film> filmSort = new ArrayList<>(getAllFilms());
        filmSort.sort((film1, film2) -> film2.getFilmsLike().size() - film1.getFilmsLike().size());
        Iterator<Film> filmIterator = filmSort.iterator();
        while (filmIterator.hasNext() && mostPopularFilms.size() < countLikes) {
            mostPopularFilms.add(filmIterator.next());
        }
        return mostPopularFilms;
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(allGenre.values());
    }

    @Override
    public Genre getGenreById(int genreId) {
        return allGenre.get(genreId);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>(allMpa.values());
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        return allMpa.get(mpaId);
    }
}
