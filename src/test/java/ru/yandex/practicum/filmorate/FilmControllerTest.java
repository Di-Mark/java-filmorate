package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class FilmControllerTest {

    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
    FilmController filmController = new FilmController(filmService);
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService);

    @Test
    public void createFilmTest() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 2, 21))
                .duration(4)
                .build();
        filmController.createFilm(film);
        film.setId(1);
        List<Film> mem = filmController.findAllFilms();
        assertEquals(filmController.findAllFilms().get(0), film);
    }

    @Test
    public void patchFilmTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        Film apd = Film.builder()
                .id(1)
                .name("Film Updated")
                .description("New film update decription")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(100)
                .build();
        filmController.patchFilm(apd);
        assertEquals(apd, filmController.findAllFilms().get(0));
    }

    @Test
    public void findAllFilmsTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .name("Film Updated")
                .description("New film update decription")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(100)
                .build();
        filmController.createFilm(filmTwo);
        filmOne.setId(1);
        filmTwo.setId(2);
        Film[] filmsArr = new Film[2];
        filmsArr[0] = filmOne;
        filmsArr[1] = filmTwo;
        Film[] result = filmController.findAllFilms().toArray(new Film[0]);
        assertArrayEquals(filmsArr, result);
    }

    @Test
    public void createFilmWithEmptyNameTest() {
        Film filmOne = Film.builder()
                .name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.createFilm(filmOne));
    }

    @Test
    public void createFilmWith200CharactersLongTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjy" +
                        "uvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        filmOne.setId(1);
        assertEquals(filmOne, filmController.findAllFilms().get(0));
    }

    @Test
    public void createFilmWith201CharactersLongTest() {
        Film filmOne = Film.builder()
                .name("i am robot")
                .description("adipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjy" +
                        "uvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlg1")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.createFilm(filmOne));
    }

    @Test
    public void createFilmWhenReleaseDateEarlyDateLimitTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.createFilm(filmOne));
    }

    @Test
    public void createFilmWhenReleaseDateLimitTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        filmOne.setId(1);
        assertEquals(filmOne, filmController.findAllFilms().get(0));
    }

    @Test
    public void createFilmWithNegativeDuration() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(-1)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.createFilm(filmOne));
    }

    @Test
    public void patchFilmWithEmptyNameTest() throws ValidationException {
        Film filmOne = Film.builder()
                .name("name")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .id(1)
                .name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.patchFilm(filmTwo));
    }

    @Test
    public void patchFilmWith200CharactersLongTest() throws ValidationException {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("yuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        Film filmTwo = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjy" +
                        "uvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        filmController.patchFilm(filmTwo);
        assertEquals(filmTwo, filmController.findAllFilms().get(0));
    }

    @Test
    public void patchFilmWith201CharactersLongTest() throws ValidationException {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("yuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        Film filmTwo = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("1adipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjy" +
                        "uvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        assertThrowsExactly(ValidationException.class, () -> filmController.patchFilm(filmTwo));
    }

    @Test
    public void patchFilmWhenReleaseDateEarlyDateLimitTest() throws ValidationException {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.patchFilm(filmTwo));
    }

    @Test
    public void patchFilmWhenReleaseDateLimitTest() throws ValidationException {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1995, 12, 28))
                .duration(100)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1995, 12, 28))
                .duration(100)
                .build();
        assertEquals(filmTwo, filmController.findAllFilms().get(0));
    }

    @Test
    public void patchFilmWithNegativeDuration() throws ValidationException {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(-1)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.patchFilm(filmTwo));
    }

    @Test
    public void likeFilmTest() {
        Film filmOne = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        filmController.createFilm(filmOne);
        Film filmTwo = Film.builder()
                .name("nisi eiusmod")
                .description("yuvdlgadipisicingghjyuvdlg")
                .releaseDate(LocalDate.of(1989, 12, 27))
                .duration(100)
                .build();
        filmController.createFilm(filmTwo);
        User user = User.builder()
                .name("name")
                .email("mail@")
                .login("login")
                .birthday(LocalDate.of(2001, 12, 27))
                .build();
        userController.createUser(user);
        filmController.likeFilm(2, 1);
    }
}
