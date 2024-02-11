package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@Getter
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return filmService.getFilmStorage().findAllFilms();
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) {
        return filmService.getFilmStorage().createFilm(film);
    }

    @PutMapping("/films")
    public Film patchFilm(@RequestBody Film film) {
        return filmService.getFilmStorage().patchFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film fildFilm(@PathVariable("id") Integer id) {
        return filmService.getFilmStorage().getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilmsList(@RequestParam(value = "count", required = false) Integer count) {
        if (count == null) {
            return filmService.getTenFirstFilms();
        } else return filmService.getPopularFilmsList(count);
    }
}