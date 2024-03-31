package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.enums.GenreName;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;


    @Test
    public void getGenreTest() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        Genre genre = genreDbStorage.getGenre(1);
        assertEquals(new Genre(1, GenreName.Комедия), genre);
    }

    @Test
    public void findAllGenresTest() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        List<Genre> genreList = genreDbStorage.findAllGenres();
        List<Genre> genreList1 = new ArrayList<>();
        genreList1.add(new Genre(1, GenreName.Комедия));
        genreList1.add(new Genre(2, GenreName.Драма));
        genreList1.add(new Genre(3, GenreName.Мультфильм));
        genreList1.add(new Genre(4, GenreName.Триллер));
        genreList1.add(new Genre(5, GenreName.Документальный));
        genreList1.add(new Genre(6, GenreName.Боевик));
        Genre[] result = genreList.toArray(new Genre[0]);
        Genre[] arr = genreList1.toArray(new Genre[0]);
        assertArrayEquals(arr, result);
    }

    @Test
    public void getErrorGenreTest() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        assertThrowsExactly(NotFoundException.class, () -> genreDbStorage.getGenre(500));
    }
}
