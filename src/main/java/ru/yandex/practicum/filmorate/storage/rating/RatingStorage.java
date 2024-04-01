package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingStorage {
    List<Mpa> findAllRating();

    Mpa getRating(Integer id);
}
