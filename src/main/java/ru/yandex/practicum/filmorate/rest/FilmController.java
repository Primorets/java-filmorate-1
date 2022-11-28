package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.FilmsAndUsersValidationException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("films")
public class FilmController {

    private Map<Integer, Film> allFilms = new HashMap<>();
    private int id;

    @GetMapping
    public List<Film> getFilms(Film film) {
        List<Film> filmsList = new ArrayList<>(allFilms.values());
        log.info("Получен запрос. Список всех фильмов");
        return filmsList;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(++id);
        validateFilm(film);
        allFilms.put(id, film);
        log.info("Добавлен фильм: " +
                film.getName() + " ID: " + film.getId() + " Описание: " +
                film.getDescription() + " Дата выхода: " + film.getReleaseDate() +
                " Продолжительность: " + film.getDuration());
        return film;
    }

    @PutMapping
    public Film updateUser(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (!allFilms.containsKey(film.getId())) {
            throw new ValidationException("invalid id");
        }
        allFilms.put(film.getId(), film);
        log.info("Обновлены данные по фильму: " +
                film.getName() + " ID: " + film.getId() + " Описание: " +
                film.getDescription() + " Дата выхода: " + film.getReleaseDate() +
                " Продолжительность: " + film.getDuration());
        return film;
    }

    protected void validateFilm(Film film) throws FilmsAndUsersValidationException {
        if (film.getDescription().length() > 200) {
            throw new FilmsAndUsersValidationException("Описание фильма больше 200 символов. " +
                    "Необходимо сократить описание.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
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
