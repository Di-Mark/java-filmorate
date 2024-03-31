package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import java.time.LocalDate;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;


    @Test
    public void createWithFailEmailTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.createUser(user1));
    }

    @Test
    public void createWithFailLoginTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("lo g");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.createUser(user1));
    }

    @Test
    public void createWithFailBirthdayTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2077, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.createUser(user1));
    }


    @Test
    public void patchWithFailBirthdayTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        User user2 = new User();
        user2.setId(1);
        user2.setEmail("email@");
        user2.setLogin("log");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2077, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.patchUser(user2));
    }

}
