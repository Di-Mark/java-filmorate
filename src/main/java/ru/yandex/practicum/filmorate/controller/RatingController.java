package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.util.List;

@Slf4j
@Getter
@RestController
public class RatingController {
    private final RatingDbStorage ratingDbStorage;

    @Autowired
    public RatingController(RatingDbStorage ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    @GetMapping("/mpa")
    List<Rating> findAllRatings() {
        return ratingDbStorage.findAllRating();
    }

    @GetMapping("/mpa/{id}")
    Rating getRating(@PathVariable("id") Integer id) {
        return ratingDbStorage.getRating(id);
    }
}
