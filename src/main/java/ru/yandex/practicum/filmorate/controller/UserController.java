package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    int idUser = 1;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        log.info("Список юзеров успешно получен");
        return List.copyOf(users.values());
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) throws ValidationException {
        checkUser(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(idUser);
        users.put(idUser, user);
        idUser++;
        log.info("Юзер успешно создан");
        return user;
    }

    @PutMapping("/users")
    public User patchUser(@RequestBody User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().equals("")) {
            log.info("почта пользователя пустая или неправильного формата");
            throw new ValidationException("почта пользователя пустая или неправильного формата");
        } else users.get(user.getId()).setEmail(user.getEmail());

        if (user.getLogin().equals("") || user.getLogin().contains(" ") || user.getLogin() == null) {
            log.info("Логин пользователя пустой или содержит пробелы");
            throw new ValidationException("Логин пользователя пустой или содержит пробелы");
        } else users.get(user.getId()).setLogin(user.getLogin());

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("День рождение пользователя не может быть в будущем");
            throw new ValidationException("День рождение пользователя не может быть в будущем");
        } else users.get(user.getId()).setBirthday(user.getBirthday());

        if (user.getName() == null) {
            users.get(user.getId()).setName(user.getLogin());
        } else users.get(user.getId()).setName(user.getName());

        log.info("Юзер успешно обновлен");
        return users.get(user.getId());
    }

    private void checkUser(User user) throws ValidationException {
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
}
