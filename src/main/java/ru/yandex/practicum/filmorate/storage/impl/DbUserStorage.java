package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.mapping.MapRowForUser;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Component("dbUserStorage")
@Repository
public class DbUserStorage implements UserStorage {
    @Qualifier("getTemplate")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sqlQuery = "INSERT INTO users" +
                "(user_name," +
                " email," +
                " login," +
                " birthday )" +
                " VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getLogin());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                preparedStatement.setNull(4, Types.DATE);
            } else {
                preparedStatement.setDate(4, Date.valueOf(birthday));
            }
            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        String sqlQuery = "DELETE FROM users " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User get(int userId) {
        final String sqlQuery = "SELECT user_id," +
                " user_name," +
                " email," +
                " login," +
                " birthday" +
                " FROM users";
        List<User> users = jdbcTemplate.query(sqlQuery, new MapRowForUser());
        if (users.size() < userId) {
            throw new NotFoundException("Не найден ID.");
        }
        final String sqlQueryForObject = "SELECT user_id," +
                " user_name," +
                " email," +
                " login," +
                " birthday" +
                " FROM users" +
                " WHERE user_id =?";
        return jdbcTemplate.queryForObject(sqlQueryForObject, new MapRowForUser(), userId);
    }

    @Override
    public List<User> getCommonFriends(int id, int friendId) {
        final String sqlQuery = "SELECT * " +
                "FROM USERS u " +
                "JOIN (SELECT friend_id " +
                "FROM friends " +
                "WHERE user_one_id=?) f ON u.user_id=f.friend_id " +
                "JOIN (SELECT friend_id" +
                " FROM friends " +
                "WHERE user_one_id=?) o ON u.user_id=o.friend_id";

        return jdbcTemplate.query(sqlQuery, new MapRowForUser(), id, friendId);
    }

    @Override
    public List<User> getAllUsersList() {
        final String sqlQuery = "SELECT user_id," +
                " login," +
                " user_name," +
                " email," +
                " birthday" +
                " FROM users";
        return jdbcTemplate.query(sqlQuery, new MapRowForUser());
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET   login=?, " +
                "user_name=?, " +
                "email = ?," +
                "birthday=? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO friends " +
                "VALUES (?,?,true)";
        try {
            jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
        } catch (Exception exception) {
            throw new NotFoundException("ID пользователей не найдены.");
        }
    }

    @Override
    public void deleteFriends(User user, User friend) {
        String sqlQuery = "DELETE FROM friends" +
                " WHERE user_one_id =? " +
                "AND friend_id=?";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public List<User> getOneUserFriendsList(int userId) {
        final String sqlQueryOneUser = "SELECT * " +
                "FROM users u " +
                "JOIN (SELECT friend_id " +
                "FROM friends " +
                "WHERE user_one_id=?) f ON u.user_id=f.friend_id";
        return jdbcTemplate.query(sqlQueryOneUser, new MapRowForUser(), userId);
    }
}
