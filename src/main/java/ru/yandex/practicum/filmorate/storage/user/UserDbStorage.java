package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;


    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAllUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        checkUser(user);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        String sql = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        String sqlTemp = "SELECT MAX(USER_ID) AS user_id FROM USERS";
        Integer id = jdbcTemplate.queryForObject(sqlTemp, (rs, rowNum) -> temp(rs));
        user.setId(id);
        return user;
    }

    @Override
    public User patchUser(User user) {
        getUser(user.getId());
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().equals("")) {
            log.info("почта пользователя пустая или неправильного формата");
            throw new ValidationException("почта пользователя пустая или неправильного формата");
        }

        if (user.getLogin().equals("") || user.getLogin().contains(" ") || user.getLogin() == null) {
            log.info("Логин пользователя пустой или содержит пробелы");
            throw new ValidationException("Логин пользователя пустой или содержит пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("День рождение пользователя не может быть в будущем");
            throw new ValidationException("День рождение пользователя не может быть в будущем");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ?" +
                "where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        log.info("Юзер успешно обновлен");
        return user;
    }

    @Override
    public User getUser(int id) {
        String sql = "select * from users where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (sqlRowSet.next()) {
            log.info("Запрос на получению пользователя по id успешно произведен");
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
        } else {
            log.info("Фильма с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().equals("")) {
            log.info("почта пользователя пустая или неправильного формата");
            throw new ValidationException("почта пользователя пустая или неправильного формата");
        } else if (user.getLogin().equals("") || user.getLogin().contains(" ") || user.getLogin() == null) {
            log.info("Логин пользователя пустой или содержит пробелы");
            throw new ValidationException("Логин пользователя пустой или содержит пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("День рождение пользователя не может быть в будущем");
            throw new ValidationException("День рождение пользователя не может быть в будущем");
        }
    }

    private Integer temp(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }
}
