package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Getter
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmDbStorage;
        this.userStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void likeFilm(int id, int userId) {
        filmStorage.getFilm(id);
        userStorage.getUser(userId);
        String sqlTest = "Select * From film_likes where film_id = ? and user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, id, userId);
        if (sqlRowSet.next()) {
            throw new ValidationException("лайк уже поставлен");
        } else {
            String sqlInsert = "insert into film_likes (user_id,film_id) values(?,?)";
            jdbcTemplate.update(sqlInsert, userId, id);
        }
    }

    public void deleteLikeFromFilm(int id, int userId) {
        filmStorage.getFilm(id);
        userStorage.getUser(userId);
        String sqlTest = "Select * From film_likes where film_id = ? and user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, id, userId);
        if (sqlRowSet.next()) {
            throw new ValidationException("лайк здесь не стоит");
        } else {
            String sqlDelete = "delete from film_likes where user_id = ? and film_id = ?";
            jdbcTemplate.update(sqlDelete, userId, id);
        }
    }

    public List<Film> getPopularFilmsList(int count) {
        String sql = "SELECT * " +
                "FROM FILMS " +
                "WHERE FILM_ID IN " +
                "(SELECT film_id " +
                "FROM FILM_LIKES  " +
                "GROUP BY FILM_ID  " +
                "ORDER BY SUM(USER_ID) " +
                "LIMIT ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        List<Integer> genreList = getGenreById(id);
        if (genreList.size() == 0) {
            genreList = null;
        } else {
            String sqlTest = "select * from genres where genre_id = ?";
            for (Integer tempInt : genreList) {
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, tempInt);
                if (!sqlRowSet.next()) {
                    throw new ValidationException("такого жанра нет");
                }
            }
        }
        Integer rating = null;
        if (rs.getString("rating_id") != null) {
            String sqlTest = "select * from ratings where rating_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest,
                    Integer.parseInt(rs.getString("rating_id")));
            if (sqlRowSet.next()) {
                rating = Integer.parseInt(rs.getString("rating_id"));
            }
        }
        return new Film(id, name, description, releaseDate, duration, rating, genreList);
    }

    private List<Integer> getGenreById(Integer id) {
        String sql = "SELECT Genre_id " +
                "FROM FILM_GENRE " +
                "WHERE FILM_ID  = ? " +
                "ORDER BY GENRE_ID  ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> temp(rs), id);
    }

    private Integer temp(ResultSet rs) throws SQLException {
        return rs.getInt("genre_id");
    }
}
