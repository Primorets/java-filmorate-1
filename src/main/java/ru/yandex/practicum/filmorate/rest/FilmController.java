package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("films")
public class FilmController {

    @Autowired
    public FilmService filmService;
    private static final int MAX_DESCRIPTION_SIMBOLS = 200;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос. Список всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public Film filmOnId(@PathVariable int id) {
        log.info("Получен запрос. Фильм с ID: " + id);
        return filmService.getFilmOnId(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        log.info("Добавлен фильм: " +
                film.getName() + " ID: " + film.getId() + " Описание: " +
                film.getDescription() + " Дата выхода: " + film.getReleaseDate() +
                " Продолжительность: " + film.getDuration());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateUser(@Valid @RequestBody Film film) {
        validateFilm(film);
        filmService.getFilmOnId(film.getId());
        filmService.createFilm(film);
        log.info("Обновлены данные по фильму: " +
                film.getName() + " ID: " + film.getId() + " Описание: " +
                film.getDescription() + " Дата выхода: " + film.getReleaseDate() +
                " Продолжительность: " + film.getDuration());
        return film;
    }

    @GetMapping("popular")
    public List<Film> getPopularFilmsWithLikes(@RequestParam(value = "count", defaultValue = "10", required = false) String count) {
        log.info("Получен запрос. Список из " + count + "самых популярных фильмов.");
        return filmService.getMostPopularFilms(Integer.parseInt(count));
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Фильму ID: " + id + "поставлен лайк от пользователя: " + userId);
        return filmService.getFilmOnId(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
        log.info("У фильма ID: " + id + " удалён лайк от пользователя: " + userId);
        return filmService.getFilmOnId(id);
    }

    protected void validateFilm(Film film) throws FilmsAndUsersValidationException {
        if (film.getDescription().length() > MAX_DESCRIPTION_SIMBOLS) {
            throw new FilmsAndUsersValidationException("Описание фильма больше 200 символов. " +
                    "Необходимо сократить описание.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new FilmsAndUsersValidationException("Не верная дата выхода фильма. " +
                    "Дата выхода фильма должна быть не раньше, чем 28 декабря 1895 года.");
        }
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            throw new FilmsAndUsersValidationException("Не верное название. Название фильма не может быть пустым.");
        }
        if (film.getDuration() < 0) {
            throw new FilmsAndUsersValidationException("Не верная продолжительность фильма. " +
                    "Продолжительность фильма не может быть меньше 0.");
        }
    }
}
