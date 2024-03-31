package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Slf4j
@Getter
@RestController
public class GenreController {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return genreDbStorage.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    Genre getGenre(@PathVariable("id") Integer id) {
        return genreDbStorage.getGenre(id);
    }
}
