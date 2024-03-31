package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.GenreName;

@Builder
@Data
public class Genre {
    private Integer id;
    private GenreName name;

    public Genre(Integer id, GenreName name) {
        this.id = id;
        this.name = name;
    }
}
