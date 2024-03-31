package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private int idFilm = 1;

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
        film.setId(idFilm);
        if (film.getMpa() != null) {
            String sqlTest = "select * from ratings where rating_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, film.getMpa());
            if (!sqlRowSet.next()) {
                throw new ValidationException("такого рейтинга нет");
            }
        }
        if (film.getGenres() != null) {
            String sql2 = "insert into film_genre (film_id,genre_id) values(?,?)";
            String sqlTest = "select * from genres where genre_id = ?";
            for (Integer genre : film.getGenres()) {
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, genre);
                if (!sqlRowSet.next()) {
                    throw new ValidationException("такого жанра нет");
                }
            }
        }
        String sql = "insert into films (film_id,name,description,release_date,duration,rating_id) values(?,?,?,?,?,?)";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa());
        if (film.getGenres() != null) {
            for (Integer temp : film.getGenres()) {
                String sql2 = "insert into film_genre (film_id,genre_id) values(?,?)";
                jdbcTemplate.update(sql2, film.getId(), temp);
            }
        }
        idFilm++;
        log.info("Фильм успешно создан");
        return film;
    }

    @Override
    public Film patchFilm(Film film) {
        Film newFilm = new Film();
        newFilm.setId(film.getId());
        if (film.getName() != null) {
            if (!film.getName().equals("")) {
                newFilm.setName(film.getName());
            } else {
                log.info("Название фильма не может быть пустым");
                throw new ValidationException("Название фильма не может быть пустым");
            }
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() <= 200) {
                newFilm.setDescription(film.getDescription());
            } else {
                log.info("Превышена допустимая длина символов в описании фильма");
                throw new ValidationException("Превышена допустимая длина символов в описании фильма");
            }
        }
        if (film.getReleaseDate() != null) {
            if (!film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                newFilm.setReleaseDate(film.getReleaseDate());
            } else {
                log.info("Дата релиза фильма слишком старая");
                throw new ValidationException("Дата релиза фильма слишком старая");
            }
        }
        if (film.getDuration() != null) {
            if (film.getDuration() >= 0) {
                newFilm.setDuration(film.getDuration());
            } else {
                log.info("Продолжительность фильма не может быть отрицательной");
                throw new ValidationException("Продолжительность фильма не может быть отрицательной");
            }
        }
        if (film.getMpa() != null) {
            String sqlTest = "select * from ratings where rating_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, film.getMpa());
            if (sqlRowSet.next()) {
                newFilm.setMpa(film.getMpa());
            } else throw new ValidationException("такого рейтинга нет");
        }
        if (film.getGenres() != null) {
            String sqlInsert = "insert into film_genre (film_id,genre_id) values(?,?)";
            String sqlTest = "select * from genres where genre_id = ?";
            for (Integer genre : film.getGenres()) {
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlTest, genre);
                if (!sqlRowSet.next()) {
                    throw new ValidationException("такого жанра нет");
                }
            }
            String sqlDelete = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlDelete, newFilm.getId());
            for (Integer temp : film.getGenres()) {
                jdbcTemplate.update(sqlInsert, newFilm.getId(), temp);
            }
        } else {
            String sqlDelete = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlDelete, newFilm.getId());
        }
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sql, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getMpa(), newFilm.getId());
        newFilm.setGenres(film.getGenres());
        log.info("Фильм успешно обновлен");
        return newFilm;
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
