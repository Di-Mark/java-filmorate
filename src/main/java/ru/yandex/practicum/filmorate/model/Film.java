package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


/**
 * Film.
 */

@Builder
@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres;

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                Integer duration, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film() {
    }
}