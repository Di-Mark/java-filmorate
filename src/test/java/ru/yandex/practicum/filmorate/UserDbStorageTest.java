package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void createUserTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        user1.setId(1);
        assertEquals(user1, userDbStorage.getUser(1));
    }

    @Test
    public void patchUserTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        User user2 = new User();
        user2.setId(1);
        user2.setEmail("new@");
        user2.setLogin("lnewog");
        user2.setName("nanrewme");
        user2.setBirthday(LocalDate.of(2001, 12, 2));
        userDbStorage.patchUser(user2);
        assertEquals(user2, userDbStorage.getUser(1));
    }

    @Test
    public void findAllUsersTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        User user2 = new User();
        user2.setEmail("new@");
        user2.setLogin("lnewog");
        user2.setName("nanrewme");
        user2.setBirthday(LocalDate.of(2001, 12, 2));
        userDbStorage.createUser(user2);
        user1.setId(1);
        user2.setId(2);
        List<User> temp = new ArrayList<>();
        temp.add(user1);
        temp.add(user2);
        User[] arrT = temp.toArray(new User[0]);
        User[] res = userDbStorage.findAllUsers().toArray(new User[0]);
        assertArrayEquals(arrT, res);
    }

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
    public void createWithEmptyNameTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        user1.setId(1);
        user1.setName("log");
        assertEquals(user1, userDbStorage.getUser(1));
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
    public void patchWithFailEmailTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.createUser(user1);
        User user2 = new User();
        user2.setId(1);
        user2.setEmail("email");
        user2.setLogin("log");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2000, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.patchUser(user2));
    }

    @Test
    public void patchWithFailLogTest() {
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
        user2.setLogin("l og");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2000, 12, 2));
        assertThrowsExactly(ValidationException.class, () -> userDbStorage.patchUser(user2));
    }

    @Test
    public void patchWithEmptyNameTest() {
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
        user2.setName("");
        user2.setBirthday(LocalDate.of(2000, 12, 2));
        userDbStorage.patchUser(user2);
        user2.setName("log");
        User res = userDbStorage.getUser(1);
        assertEquals(user2, res);
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

    @Test
    public void addFriendTest() {
        UserService userService = new UserService(new UserDbStorage(jdbcTemplate), jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userService.getUserStorage().createUser(user1);
        User user2 = new User();
        user2.setEmail("nemail@");
        user2.setLogin("nlog");
        user2.setName("nname");
        user2.setBirthday(LocalDate.of(2022, 12, 2));
        userService.getUserStorage().createUser(user2);
        userService.addFriend(1, 2);
        user2.setId(2);
        List<User> temp = new ArrayList<>();
        temp.add(user2);
        User[] arrT = temp.toArray(new User[0]);
        User[] res = userService.getFriendList(1).toArray(new User[0]);
        assertArrayEquals(arrT, res);
    }

    @Test
    public void deleteFriendTest() {
        UserService userService = new UserService(new UserDbStorage(jdbcTemplate), jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        userService.getUserStorage().createUser(user1);
        User user2 = new User();
        user2.setEmail("nemail@");
        user2.setLogin("nlog");
        user2.setName("nname");
        user2.setBirthday(LocalDate.of(2022, 12, 2));
        userService.getUserStorage().createUser(user2);
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);
        user2.setId(2);
        List<User> temp = new ArrayList<>();
        User[] arrT = temp.toArray(new User[0]);
        User[] res = userService.getFriendList(1).toArray(new User[0]);
        assertArrayEquals(arrT, res);
    }
}
