package ru.yandex.practicum.filmorate.storage.impl.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapRowForUser implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("user_name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .build();
    }
}
