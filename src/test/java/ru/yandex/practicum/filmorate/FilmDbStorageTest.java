package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void createFilmTest() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        film.setId(1);
        Film res = filmDbStorage.getFilm(1);
        assertEquals(film, res);
    }

    @Test
    public void patchFilmTest() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        filmDbStorage.patchFilm(newFilm);
        Film res = filmDbStorage.getFilm(1);
        assertEquals(newFilm, res);
    }

    @Test
    public void findAllFilmsTest() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        filmDbStorage.createFilm(newFilm);
        film.setId(1);
        newFilm.setId(2);
        List<Film> tempList = new ArrayList<>();
        tempList.add(film);
        tempList.add(newFilm);
        Film[] tempArr = tempList.toArray(new Film[0]);
        Film[] res = filmDbStorage.findAllFilms().toArray(new Film[0]);
        assertArrayEquals(tempArr, res);
    }

    @Test
    public void createWithFailName() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void createWithFailDesc() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("name");
        film.setDescription("adipisicingergtvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvtttttttttttttttttttttttttttttttttttttv" +
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddd" +
                "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddfffffffffffffffffffffff");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void createWithFailReleaseDate() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("name");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1190, 12, 2));
        film.setDuration(100);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void createWithFailDuration() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("name");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(-100);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void patchFilmWithFailName() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void patchFilmWithFailDesc() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("newbrggrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrbbbbbbbbbb" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void patchWithFailReleaseDate() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(1000, 12, 2));
        newFilm.setDuration(110);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void patchWithFailDuration() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(-110);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void createFilmWithMpa() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        film.setMpa(1);
        filmDbStorage.createFilm(film);
        film.setId(1);
        assertEquals(film, filmDbStorage.getFilm(1));
    }

    @Test
    public void createFilmWithGenres() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> temp = new ArrayList<>();
        temp.add(1);
        temp.add(2);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        film.setMpa(1);
        film.setGenres(temp);
        filmDbStorage.createFilm(film);
        film.setId(1);
        assertEquals(film, filmDbStorage.getFilm(1));
    }

    @Test
    public void createWithFailMpa() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> temp = new ArrayList<>();
        temp.add(1);
        temp.add(2);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        film.setMpa(100);
        film.setGenres(temp);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void createWithFailGenres() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> temp = new ArrayList<>();
        temp.add(1);
        temp.add(200);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        film.setMpa(1);
        film.setGenres(temp);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void patchWithMpa() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        newFilm.setMpa(1);
        filmDbStorage.patchFilm(newFilm);
        Film res = filmDbStorage.getFilm(1);
        assertEquals(newFilm, res);
    }

    @Test
    public void patchWithGenres() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> temp = new ArrayList<>();
        temp.add(1);
        temp.add(2);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        newFilm.setMpa(1);
        newFilm.setGenres(temp);
        filmDbStorage.patchFilm(newFilm);
        Film res = filmDbStorage.getFilm(1);
        assertEquals(newFilm, res);
    }

    @Test
    public void patchWithFailMpa() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        newFilm.setMpa(100);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void patchWithFailGenres() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> temp = new ArrayList<>();
        temp.add(1);
        temp.add(200);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        newFilm.setMpa(1);
        newFilm.setGenres(temp);
        assertThrowsExactly(ValidationException.class, () -> filmDbStorage.patchFilm(newFilm));
    }

    @Test
    public void getPopularFilmsListTest() {
        FilmService filmService = new FilmService(new FilmDbStorage(jdbcTemplate), new UserDbStorage(jdbcTemplate),
                jdbcTemplate);
        UserService userService = new UserService(new UserDbStorage(jdbcTemplate), jdbcTemplate);
        User user1 = new User();
        user1.setEmail("email@");
        user1.setLogin("log");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 12, 2));
        User user2 = new User();
        user2.setEmail("email@");
        user2.setLogin("log");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2000, 12, 2));
        userService.getUserStorage().createUser(user1);
        userService.getUserStorage().createUser(user2);
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1990, 12, 2));
        film.setDuration(100);
        filmService.getFilmStorage().createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 12, 2));
        newFilm.setDuration(110);
        filmService.getFilmStorage().createFilm(newFilm);
        film.setId(1);
        newFilm.setId(2);
        List<Film> temp = new ArrayList<>();
        temp.add(film);
        temp.add(newFilm);
        filmService.likeFilm(1, 1);
        filmService.likeFilm(1, 2);
        filmService.likeFilm(2, 2);
        Film[] tempArr = temp.toArray(new Film[0]);
        Film[] res = filmService.getPopularFilmsList(2).toArray(new Film[0]);
        assertArrayEquals(tempArr, res);
    }
}
