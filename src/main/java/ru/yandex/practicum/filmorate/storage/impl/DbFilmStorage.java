package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.mapping.MapRowForFilm;
import ru.yandex.practicum.filmorate.storage.impl.mapping.MapRowForGenres;
import ru.yandex.practicum.filmorate.storage.impl.mapping.MapRowForMpa;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("dbFilmStorage")
@Repository
public class DbFilmStorage implements FilmStorage {

    @Qualifier("getTemplate")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Film get(int filmId) {
        try {
            final String sqlQuery = "SELECT * " +
                    "FROM films f " +
                    "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                    "WHERE film_id = ?";
            final String sqlQueryForCheckId = "SELECT * " +
                    "FROM films f " +
                    "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id ";
            if (jdbcTemplate.query(sqlQueryForCheckId, new MapRowForFilm()).size() < filmId) {
                throw new NotFoundException("Не найден ID.");
            }
            Optional<Film> filmOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new MapRowForFilm(), filmId));
            if (filmOptional.isPresent()) {
                Film film = filmOptional.get();
                List<Genre> genres = getGenreByIdForFilms(filmId);
                if (genres.size() > 0) {
                    film.setGenres(new ArrayList<>());
                    genres.forEach(genre -> film.getGenres().add(genre));
                }
                return film;
            }
            return jdbcTemplate.queryForObject(sqlQuery, new MapRowForFilm(), filmId);

        } catch (NotFoundException notFoundException) {
            throw new NotFoundException("Не найден ID.");
        }
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO films" +
                "(film_name," +
                "description," +
                " release_date," +
                " duration, " +
                " mpa_id) " +
                "VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                preparedStatement.setNull(3, Types.DATE);
            } else {
                preparedStatement.setDate(3, Date.valueOf(releaseDate));
            }
            return preparedStatement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return addGenreForFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQueryForDeleteGenre = "DELETE FROM film_genres" +
                " WHERE film_id=?";
        String sqlQueryForUpdateFilm = "UPDATE films " +
                "SET film_name = ?," +
                " description = ?," +
                " release_date = ?, " +
                "duration = ?," +
                " mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryForDeleteGenre, film.getId());
        jdbcTemplate.update(sqlQueryForUpdateFilm, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        return addGenreForFilm(film);
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "INSERT INTO films_like" +
                "(film_id, " +
                "user_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, film.getId(),
                user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "DELETE FROM films_like" +
                " WHERE film_id =?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public List<Film> getLikeFromUserId0ForFilm(int filmId, int userId) {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "LEFT JOIN(SELECT film_id, " +
                "user_id  " +
                "FROM films_LIKE" +
                " GROUP BY film_id) l ON f.film_id=l.film_id " +
                "LEFT JOIN mpa m ON m.mpa_id=f.mpa_id " +
                "WHERE l.film_id=? AND l.user_id=?";
        return jdbcTemplate.query(sqlQuery, new MapRowForFilm(), filmId, userId);
    }

    @Override
    public List<Film> getAllFilms() {
        final String sqlQuery = "SELECT * " +
                "FROM films f " +
                "LEFT JOIN mpa ON mpa.mpa_id=F.mpa_id";
        List<Film> allFilms = jdbcTemplate.query(sqlQuery, new MapRowForFilm());
        List<Film> allFilmsWithGenres = new ArrayList<>();
        allFilms.forEach(film -> allFilmsWithGenres.add(get(film.getId())));
        return allFilmsWithGenres;
    }

    @Override
    public List<Film> getMostPopularFilms(int countLikes) {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "LEFT JOIN(SELECT film_id," +
                " count(*) likes_count  " +
                "FROM FILMS_LIKE" +
                " GROUP BY film_id) l ON f.film_id=l.film_id " +
                "LEFT JOIN mpa m ON m.mpa_id=f.mpa_id " +
                "ORDER BY " +
                "l.likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, new MapRowForFilm(), countLikes);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * " +
                "FROM genres";
        return jdbcTemplate.query(sqlQuery, new MapRowForGenres());
    }

    public List<Genre> getGenreByIdForFilms(int genreId) {
        String sqlQuery = "SELECT f.genre_id, " +
                "genre_name " +
                "FROM film_genres f " +
                "LEFT JOIN (SELECT * " +
                "FROM genres) g " +
                "ON f.genre_id=g.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, new MapRowForGenres(), genreId);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "WHERE genre_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, new MapRowForGenres(), genreId);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * " +
                "FROM MPA";
        return jdbcTemplate.query(sqlQuery, new MapRowForMpa());
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        String sqlQuery = "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, new MapRowForMpa(), mpaId);
    }

    private Film addGenreForFilm(Film film) {
        String sqlQuery = "INSERT INTO film_genres (film_id," +
                " genre_id) " +
                "VALUES ( ?,? )";
        List<Genre> uniqGenre = film.getGenres()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        uniqGenre.forEach(x -> jdbcTemplate.update(sqlQuery, film.getId(), x.getId()));
        film.setGenres(uniqGenre);
        return film;
    }
}
