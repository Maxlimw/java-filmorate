package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDb implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM RATING;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa findMpaById(Integer id) throws MpaNotFoundException {
        String sqlQuery = "SELECT * FROM RATING WHERE RATING_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("RATING_ID"), mpaRows.getString("RATING_NAME"));
            log.info("Найден рейтинг с id {}", id);
            return mpa;
        }
        log.warn("Рейтинг с id {} не найден", id);
        throw new MpaNotFoundException("Рейтинг с id " + id + " не найден");
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME"));
    }
}