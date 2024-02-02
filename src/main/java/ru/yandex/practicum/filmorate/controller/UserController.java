package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    int idUser = 1;

    @GetMapping("/users")
    public List<User> findAllUsers() {
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
        return user;
    }

    @PutMapping("/users")
    public User patchUser(@RequestBody User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().equals("")) {
            throw new ValidationException("почта пользователя пустая или неправильного формата");
        } else users.get(user.getId()).setEmail(user.getEmail());

        if (user.getLogin().equals("") || user.getLogin().contains(" ") || user.getLogin() == null) {
            throw new ValidationException("Логин пользователя пустой или содержит пробелы");
        } else users.get(user.getId()).setLogin(user.getLogin());

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождение пользователя не может быть в будущем");
        } else users.get(user.getId()).setBirthday(user.getBirthday());

        if (user.getName() == null) {
            users.get(user.getId()).setName(user.getLogin());
        } else users.get(user.getId()).setName(user.getName());

        return users.get(user.getId());
    }

    private void checkUser(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().equals("")) {
            throw new ValidationException("почта пользователя пустая или неправильного формата");
        } else if (user.getLogin().equals("") || user.getLogin().contains(" ") || user.getLogin() == null) {
            throw new ValidationException("Логин пользователя пустой или содержит пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождение пользователя не может быть в будущем");
        }
    }
}
