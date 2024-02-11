package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int idUser = 1;

    @Override
    public List<User> findAllUsers() {
        log.info("Список юзеров успешно получен");
        return List.copyOf(users.values());
    }

    @Override
    public User createUser(User user) {
        checkUser(user);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        user.setId(idUser);
        users.put(idUser, user);
        idUser++;
        log.info("Юзер успешно создан");
        return user;
    }

    @Override
    public User patchUser(User user) {
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

    @Override
    public User getUser(int id) {
        if (users.containsKey(id)) {
            log.info("Запрос на получению пользователя по id успешно произведен");
            return users.get(id);
        } else {
            log.info("Фильма с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
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
}
