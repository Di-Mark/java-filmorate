package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Mpa {
    private Integer id;
    private String name;

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Mpa(Integer id) {
        this.id = id;
    }
}
