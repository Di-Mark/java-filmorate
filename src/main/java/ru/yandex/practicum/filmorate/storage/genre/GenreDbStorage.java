package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.GenreName;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql,(rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getGenre(Integer id) {
        String sql = "select * from genres where genre_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if(sqlRowSet.next()){
            log.info("Запрос на получению пользователя по id успешно произведен");
            return  jdbcTemplate.queryForObject(sql,(rs, rowNum) -> makeGenre(rs),id);
        }else {
            log.info("Фильма с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException{
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, GenreName.valueOf(name));
    }
}
