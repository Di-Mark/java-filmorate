package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
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

    public void deleteLikeFromFilm(Integer id, Integer userId) {
        String sqlTest = "Select * From film_likes where film_id = ? and user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, id, userId);
        if (!sqlRowSet.next()) {
            return;
        }
        String sqlDelete = "delete from film_likes where user_id = ? and film_id = ?";
        jdbcTemplate.update(sqlDelete, userId, id);
    }

    public List<Film> getPopularFilmsList(int count) {
        String sql = "SELECT FILM_LIKES.film_id, Films.NAME ,FILMS.DESCRIPTION ,FILMS.RELEASE_DATE , " +
                "FILMS.DURATION , FILMS.RATING_ID ,COUNT(USER_ID) " +
                "FROM FILMS  " +
                "INNER JOIN FILM_LIKES ON FILM_LIKES.FILM_ID = FILMS.FILM_ID " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        return result;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        List<Genre> genreList = new ArrayList<>();
        List<Integer> idGenreList = getGenreById(id);
        if (idGenreList.size() == 0) {
            genreList = null;
        } else {
            String sqlTest = "SELECT genre_id FROM GENRES";
            List<Integer> allGenre = jdbcTemplate.query(sqlTest, (rs1, rowNum) -> temp(rs1, "genre_id"));
            for (Integer genre : idGenreList) {
                if (!allGenre.contains(genre)) {
                    throw new ValidationException("такого жанра нет");
                }
                genreList.add(new Genre(genre));
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
        return new Film(id, name, description, releaseDate, duration, new Mpa(rating), genreList);
    }

    private List<Integer> getGenreById(Integer id) {
        String sql = "SELECT Genre_id " +
                "FROM FILM_GENRE " +
                "WHERE FILM_ID  = ? " +
                "ORDER BY GENRE_ID  ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> temp(rs, "genre_id"), id);
    }

    private Integer temp(ResultSet rs, String column) throws SQLException {
        return rs.getInt(column);
    }
}
