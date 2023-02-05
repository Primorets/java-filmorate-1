package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    public FilmService filmService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос. Список всех жанров");
        return filmService.getAllGenre();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Получен запрос. Список жанра с ID:");
        return filmService.getGenreById(id);
    }
}
