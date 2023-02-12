package ru.yandex.practicum.filmorate.storage.impl.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapRowForFilm implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        try {
            Mpa mpa = new Mpa();
            Film film = new Film();
            film.setId(resultSet.getInt("film_id"));
            film.setName(resultSet.getString("film_name"));
            film.setDescription(resultSet.getString("description"));
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            film.setDuration(resultSet.getInt("duration"));
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
            return film;
        } catch (NotFoundException notFoundException) {
            throw new NotFoundException("Ошибка данных.");
        }
    }
}
