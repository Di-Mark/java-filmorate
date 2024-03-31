package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAllRating() {
        String sql = "select * from ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Mpa getRating(Integer id) {
        String sql = "select * from ratings where rating_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (sqlRowSet.next()) {
            log.info("Запрос на получению пользователя по id успешно произведен");
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), id);

        } else {
            log.info("Рейтинга с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private Mpa makeRating(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
