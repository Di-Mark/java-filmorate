package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.RatingName;

@Builder
@Data
public class Rating {
    private Integer id;
    private RatingName name;

    public Rating(Integer id, RatingName name) {
        this.id = id;
        this.name = name;
    }
}
