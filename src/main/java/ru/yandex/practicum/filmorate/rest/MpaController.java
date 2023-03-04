package ru.yandex.practicum.filmorate.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/mpa")
public class MpaController {

    public FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получен запрос. Список всех рейтингов.");
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получен запрос. Список рейтинга по ID:");
        return filmService.getMpaById(id);
    }
}
