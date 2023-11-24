package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("userDb")
public class UserDb implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        Long maxId = jdbcTemplate.queryForObject("SELECT MAX(USER_ID) FROM \"USER\"", Long.class);
        Long newId = (maxId != null) ? maxId + 1 : 1;

        String sqlQuery = "INSERT INTO \"USER\" (USER_ID, EMAIL, LOGIN, BIRTHDAY, NAME) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, newId, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName());

        user.setId(newId);
        return getById(newId);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE \"USER\" SET EMAIL = ?, LOGIN = ?, BIRTHDAY = ?, NAME = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(),
                user.getId());
        return getById(user.getId());
    }

    @Override
    public Map<Long, User> findAll() {
        Map<Long, User> users = new HashMap<>();
        String sqlQuery = "SELECT * FROM \"USER\"";
        List<User> usersFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        for (User user : usersFromDb) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public User getById(Long id) {
        String sqlQuery = "SELECT * FROM \"USER\" WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = User.builder()
                    .email(userRows.getString("EMAIL"))
                    .login(userRows.getString("LOGIN"))
                    .name(userRows.getString("NAME"))
                    .id(userRows.getLong("USER_ID"))
                    .birthday((Objects.requireNonNull(userRows.getDate("BIRTHDAY"))).toLocalDate())
                    .build();

            log.info("Найден пользователь с id {}", id);
            return user;
        }

        log.warn("Пользователь с id {} не найден", id);
        throw new UserNotFoundException("Пользователь не найден!");
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        String sqlQuery = "INSERT INTO FRIENDSHIP (USER_FIRST_ID, USER_SECOND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFromFriends(long userId, long friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_FIRST_ID = ? AND USER_SECOND_ID = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getMutualFriends(long userId, long otherUserId) {
        String sqlQuery = "SELECT * FROM \"USER\" AS U WHERE U.USER_ID IN (SELECT F.USER_SECOND_ID " +
                "FROM FRIENDSHIP AS F WHERE F.USER_FIRST_ID = ? " +
                "INTERSECT SELECT F.USER_SECOND_ID FROM FRIENDSHIP AS F WHERE F.USER_FIRST_ID = ?);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        String sqlQuery = "SELECT * FROM \"USER\" AS U WHERE U.USER_ID IN " +
                "(SELECT F.USER_SECOND_ID FROM FRIENDSHIP AS F WHERE F.USER_FIRST_ID = ?);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .id(resultSet.getLong("USER_ID"))
                .birthday((resultSet.getDate("BIRTHDAY")).toLocalDate())
                .build();
    }
}
