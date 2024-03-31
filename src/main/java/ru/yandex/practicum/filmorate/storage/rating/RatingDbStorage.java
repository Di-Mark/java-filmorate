package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.RatingName;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RatingDbStorage implements RatingStorage{
    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> findAllRating() {
        String sql = "select * from ratings";
        return jdbcTemplate.query(sql,(rs, rowNum) -> makeRating(rs));    }

    @Override
    public Rating getRating(Integer id) {
        String sql = "select * from ratings where rating_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if(sqlRowSet.next()){
            log.info("Запрос на получению пользователя по id успешно произведен");
            return  jdbcTemplate.queryForObject(sql,(rs, rowNum) -> makeRating(rs),id);
        }else {
            log.info("Фильма с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private Rating makeRating(ResultSet rs) throws SQLException{
        Integer id = rs.getInt("rating_id");
        String name = rs.getString("name");
        switch (name){
            case "PG-13":
                return new Rating(id,RatingName.PG_13);
            case "NC-17":
                return new Rating(id,RatingName.NC_17);
        }
        return new Rating(id, RatingName.valueOf(name));
    }
}
