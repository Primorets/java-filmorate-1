package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowForUsers(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .login(resultSet.getString("LOGIN"))
                .email(resultSet.getString("EMAIL"))
                .build();
    }

    @Override
    public User get(int userId) {
        final String sqlQuery = "select USER_ID, USER_NAME,LOGIN, EMAIL, BIRTHDAY from USERS where USER_ID =?";
         return jdbcTemplate.queryForObject(sqlQuery,this::mapRowForUsers, userId);
    }

    @Override
    public List<User> getAllUsersList() {
        final String sqlQuery = "select USER_ID, USER_NAME,LOGIN, EMAIL, BIRTHDAY from USERS";
        return jdbcTemplate.query(sqlQuery,this::mapRowForUsers);
    }

    @Override
    public User save(User user) {

        String sqlQuery = "insert into USERS(EMAIL,LOGIN, USER_NAME,BIRTHDAY) values (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                preparedStatement.setNull(4, Types.DATE);
            } else {
                preparedStatement.setDate(4, Date.valueOf(birthday));
            }
            return preparedStatement;
        },keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user){
        String sqlQuery = "update USERS set EMAIL = ?,LOGIN=?, USER_NAME=?,BIRTHDAY=? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }


    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "insert into FRIENDS(USER_ONE_ID, FRIEND_ID, STATUS) values (?,?,?)";

        jdbcTemplate.update(sqlQuery, user.getId(),
                friend.getId(),
                user.getFriendsStatusMap().get(friend.getId()));
    }

    @Override
    public void deleteFriends(User user, User friend) {
        String sqlQuery = "delete from FRIENDS where USER_ONE_ID =?";

        jdbcTemplate.update(sqlQuery, user.getId());
    }
}
