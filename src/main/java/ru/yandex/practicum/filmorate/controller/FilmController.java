package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@RestController
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 1;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        log.info("Список фильмов успешно передан");
        return List.copyOf(films.values());
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        checkFilm(film);
        film.setId(idFilm);
        films.put(idFilm, film);
        idFilm++;
        log.info("Фильм успешно создан");
        return film;
    }

    @PutMapping("/films")
    public Film patchFilm(@RequestBody Film film) throws ValidationException {
        if (film.getName() != null) {
            if (!film.getName().equals("")) {
                films.get(film.getId()).setName(film.getName());
            } else {
                log.info("Название фильма не может быть пустым");
                throw new ValidationException("Название фильма не может быть пустым");
            }
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() <= 200) {
                films.get(film.getId()).setDescription(film.getDescription());
            } else {
                log.info("Превышена допустимая длина символов в описании фильма");
                throw new ValidationException("Превышена допустимая длина символов в описании фильма");
            }
        }
        if (film.getReleaseDate() != null) {
            if (!film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                films.get(film.getId()).setReleaseDate(film.getReleaseDate());
            } else {
                log.info("Дата релиза фильма слишком старая");
                throw new ValidationException("Дата релиза фильма слишком старая");
            }
        }
        if (film.getDuration() != null) {
            if (film.getDuration() >= 0) {
                films.get(film.getId()).setDuration(film.getDuration());
            } else {
                log.info("Продолжительность фильма не может быть отрицательной");
                throw new ValidationException("Продолжительность фильма не может быть отрицательной");
            }
        }
        log.info("Фильм успешно обновлен");
        return films.get(film.getId());
    }

    private void checkFilm(Film film) throws ValidationException {
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
}