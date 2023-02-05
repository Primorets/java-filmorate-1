package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapRowForFilms(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .description(resultSet.getString("DESCRIPTION"))
                .mpa(new Mpa(resultSet.getInt("MPA_ID"), resultSet.getString("MPA_NAME")))
                .genres(Set.of(new Genre(resultSet.getInt("GENRE_ID")
                        ,resultSet.getString("GENRE_NAME"))))
                .duration(resultSet.getInt("DURATION"))
                .build();
    }

    private Genre mapRowForGenre(ResultSet resultSet, int rowNum)throws SQLException{
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

    private Mpa mapRowForMpa(ResultSet resultSet, int rowNum)throws SQLException{
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    @Override
    public Film get(int filmId) {
        final String sqlQuery = "select FILM_ID, FILM_NAME,RELEASE_DATE, DESCRIPTION, DURATION from FILMS where FILM_ID =?";
        return jdbcTemplate.queryForObject(sqlQuery,this::mapRowForFilms, filmId);
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "insert into FILMS(FILM_NAME,DESCRIPTION, DURATION,RELEASE_DATE) values (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setInt(3, film.getDuration());
            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                preparedStatement.setNull(4, Types.DATE);
            } else {
                preparedStatement.setDate(4, Date.valueOf(releaseDate));
            }
            return preparedStatement;
        },keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "insert into FILMS_LIKE(FILM_ID, USER_ID) values (?,?)";
        jdbcTemplate.update(sqlQuery, film.getId(),
                user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "delete from FILMS_LIKE where FILM_ID =?";
        jdbcTemplate.update(sqlQuery,user.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        final String sqlQuery = "select FILM_ID, FILM_NAME,RELEASE_DATE, DESCRIPTION, DURATION from FILMS";
        return jdbcTemplate.query(sqlQuery,this::mapRowForFilms);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select GENRE_ID,GENRE_NAME from GENRE";
        return jdbcTemplate.query(sqlQuery,this::mapRowForGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "select GENRE_ID,GENRE_NAME from GENRE where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery,this::mapRowForGenre, genreId);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "select GENRE_ID,GENRE_NAME from GENRE";
        return jdbcTemplate.query(sqlQuery,this::mapRowForMpa);
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        String sqlQuery = "select MPA_ID,MPA_ID from MPA where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery,this::mapRowForMpa, mpaId);
    }
}
