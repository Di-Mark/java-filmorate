package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class UserControllerTest {
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService);

    @Test
    public void createUserTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        user.setId(1);
        assertEquals(user, userController.findAllUsers().get(1));
    }

    @Test
    public void createUserWithEmptyNameTest() throws ValidationException {
        User user = User.builder()
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        user.setId(1);
        user.setName(user.getLogin());
        assertEquals(user, userController.findAllUsers().get(1));
    }

    @Test
    public void patchUserTest() throws ValidationException {
        User user = User.builder()
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        User user2 = User.builder()
                .id(1)
                .name("new_name")
                .email("new_mail@")
                .login("new_login")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        userController.createUser(user);
        userController.patchUser(user2);
        assertEquals(user2, userController.findAllUsers().get(1));
    }

    @Test
    public void patchUserWithEmptyNameTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        User user2 = User.builder()
                .id(1)
                .email("new_mail@")
                .login("new_login")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        userController.createUser(user);
        userController.patchUser(user2);
        user2.setName(user.getLogin());
        assertEquals(user2, userController.findAllUsers().get(1));
    }

    @Test
    public void findAllUsersTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        User user2 = User.builder()
                .id(1)
                .name("new_name")
                .email("new_mail@")
                .login("new_login")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        userController.createUser(user);
        userController.createUser(user2);
        user.setId(1);
        user2.setId(2);
        User[] usersArr = new User[2];
        usersArr[0] = user;
        usersArr[1] = user2;
        User[] result = userController.findAllUsers().toArray(new User[0]);
        assertArrayEquals(usersArr, result);
    }

    @Test
    public void createUserWithEmptyEmailTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUserWithoutDogInEmailTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUserWithEmptyLoginTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUserWithSpaceInLoginTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("log in")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUserWithBirthdayInTheFutureTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail")
                .login("login")
                .birthday(LocalDate.of(2077, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void patchUserWithEmptyEmailTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("name_new")
                .email("")
                .login("login_new")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.patchUser(user2));
    }

    @Test
    public void patchUserWithoutDogInEmailTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("name_new")
                .email("mail_new")
                .login("login_new")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.patchUser(user2));
    }

    @Test
    public void patchUserWithEmptyLoginTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("name_new")
                .email("mail_new")
                .login("")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.patchUser(user2));
    }

    @Test
    public void patchUserWithSpaceInLoginTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("name_new")
                .email("mail_new")
                .login("new login")
                .birthday(LocalDate.of(2000, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.patchUser(user2));
    }

    @Test
    public void patchUserWithBirthdayInTheFutureTest() throws ValidationException {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("name_new")
                .email("mail_new")
                .login("new login")
                .birthday(LocalDate.of(2077, 12, 27))
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.patchUser(user2));
    }

    @Test
    public void commonFriendTest() {
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        User user2 = User.builder()
                .name("namenew")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user2);
        List<User> userList = userController.getFriendListCommonOtherUser(1, 2);
        User[] temp = new User[0];
    }
}
