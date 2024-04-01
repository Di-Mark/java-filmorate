package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.GenreName;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAllFilms() {
        log.info("Список фильмов успешно передан");
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        if (film.getMpa() != null) {
            String sqlTest = "select * from ratings where rating_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, film.getMpa().getId());
            if (!sqlRowSet.next()) {
                throw new ValidationException("такого рейтинга нет");
            }
        }
        if (film.getGenres() != null) {
            String sqlTest = "SELECT genre_id FROM GENRES";
            List<Integer> allGenre = jdbcTemplate.query(sqlTest, (rs, rowNum) -> temp(rs, "genre_id"));
            for (Genre genre : film.getGenres()) {
                if (!allGenre.contains(genre.getId())) {
                    throw new ValidationException("такого жанра нет");
                }
            }
        }
        String sql = "insert into films (name,description,release_date,duration,rating_id) values(?,?,?,?,?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        String tempSql = "SELECT MAX(film_id) AS FILM_ID  FROM FILMS";
        Integer id = jdbcTemplate.queryForObject(tempSql, (rs, rowNum) -> temp(rs, "film_id"));
        film.setId(id);
        if (film.getGenres() != null) {
            for (Genre temp : film.getGenres()) {
                String sql2 = "insert into film_genre (film_id,genre_id) values(?,?)";
                jdbcTemplate.update(sql2, film.getId(), temp.getId());
            }
        }
        log.info("Фильм успешно создан");
        return film;
    }

    @Override
    public Film patchFilm(Film film) {
        getFilm(film.getId());
        if (film.getName() == null || film.getName().equals("")) {
            log.info("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || (film.getDescription().length() > 200)) {
            log.info("Превышена допустимая длина символов в описании фильма");
            throw new ValidationException("Превышена допустимая длина символов в описании фильма");
        }
        if (film.getReleaseDate() == null ||
                (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))) {
            log.info("Дата релиза фильма слишком старая");
            throw new ValidationException("Дата релиза фильма слишком старая");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
        if (film.getMpa() != null) {
            String sqlTest = "select * from ratings where rating_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, film.getMpa().getId());
            if (!sqlRowSet.next()) {
                throw new ValidationException("такого рейтинга нет");
            }
        }
        if (film.getGenres() != null) {
            String sqlInsert = "insert into film_genre (film_id,genre_id) values(?,?)";
            String sqlTest = "SELECT genre_id FROM GENRES";
            List<Integer> allGenre = jdbcTemplate.query(sqlTest, (rs, rowNum) -> temp(rs, "genre_id"));
            for (Genre genre : film.getGenres()) {
                if (!allGenre.contains(genre.getId())) {
                    throw new ValidationException("такого жанра нет");
                }
            }
            String sqlDelete = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlDelete, film.getId());
            for (Genre temp : film.getGenres()) {
                jdbcTemplate.update(sqlInsert, film.getId(), temp.getId());
            }
        } else {
            String sqlDelete = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlDelete, film.getId());
        }
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        log.info("Фильм успешно обновлен");
        return film;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (sqlRowSet.next()) {
            log.info("Запрос на получению пользователя по id успешно произведен");
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } else {
            log.info("Фильма с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
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
                genreList.add(new Genre(genre, getNameGenre(genre)));
            }
        }
        List<Genre> resultGenre = null;
        if (genreList != null) {
            Set<Genre> genreSet = new LinkedHashSet<>(genreList);
            resultGenre = new ArrayList<>(genreSet);
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
        return new Film(id, name, description, releaseDate, duration, new Mpa(rating, getNameMpa(rating)), resultGenre);
    }

    private void checkFilm(Film film) {
        if (film.getName() == null || film.getName().equals("")) {
            log.info("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.info("Превышена допустимая длина символов в описании фильма");
            throw new ValidationException("Превышена допустимая длина символов в описании фильма");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза фильма слишком старая");
            throw new ValidationException("Дата релиза фильма слишком старая");
        } else if (film.getDuration() < 0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

    private List<Integer> getGenreById(Integer id) {
        String sql = "SELECT film_genre.Genre_id, genres.NAME " +
                "FROM FILM_GENRE " +
                "INNER JOIN GENRES ON film_genre.GENRE_ID = genres.GENRE_ID " +
                "WHERE FILM_ID  = ? " +
                "ORDER BY GENRE_ID  ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> temp(rs, "genre_id"), id);
    }

    private Integer temp(ResultSet rs, String column) throws SQLException {
        return rs.getInt(column);
    }

    private String getNameMpa(Integer id) {
        switch (id) {
            case 1:
                return "G";
            case 2:
                return "PG";
            case 3:
                return "PG-13";
            case 4:
                return "R";
            case 5:
                return "NC-17";
        }
        return null;
    }

    private GenreName getNameGenre(Integer id) {
        switch (id) {
            case 1:
                return GenreName.Комедия;
            case 2:
                return GenreName.Драма;
            case 3:
                return GenreName.Мультфильм;
            case 4:
                return GenreName.Триллер;
            case 5:
                return GenreName.Документальный;
            case 6:
                return GenreName.Боевик;
        }
        return null;
    }
}
