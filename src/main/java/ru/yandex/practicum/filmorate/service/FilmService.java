package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.*;


@Getter
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.filmStorage = inMemoryFilmStorage;
        this.userStorage = inMemoryUserStorage;
    }

    public void likeFilm(int id, int userId) {
        filmStorage.getFilm(id);
        userStorage.getUser(userId);
        if (likes.containsKey(id)) {
            likes.get(id).add(userId);
        } else {
            likes.put(id, new HashSet<>());
            likes.get(id).add(userId);
        }
    }

    public void deleteLikeFromFilm(int id, int userId) {
        filmStorage.getFilm(id);
        userStorage.getUser(userId);
        likes.get(id).remove(userId);
    }

    public List<Film> getPopularFilmsList(int count) {
        Map<Integer, Integer> unsortedMap = new LinkedHashMap<>();
        List<Film> result = new ArrayList<>();
        int myCount = 0;
        for (Integer tempId : likes.keySet()) {
            unsortedMap.put(tempId, likes.get(tempId).size());
        }
        List<Map.Entry<Integer, Integer>> entries = new
                ArrayList<Map.Entry<Integer, Integer>>(unsortedMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        });
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer,
                Integer>();
        for (Map.Entry<Integer, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        if (sortedMap.size() < count) {
            count = sortedMap.size();
        }
        for (Integer tempId : sortedMap.keySet()) {
            if (count != myCount) {
                result.add(filmStorage.getFilm(tempId));
            } else {
                break;
            }
            myCount++;
        }
        return result;
    }

    public List<Film> getTenFirstFilms() {
        List<Film> result = new ArrayList<>();
        int myCount = 0;
        int count = 10;
        if (filmStorage.findAllFilms().size() < 10) {
            count = filmStorage.findAllFilms().size();
        }
        for (Film film : filmStorage.findAllFilms()) {
            if (myCount != count) {
                result.add(film);
            } else break;
            myCount++;
        }
        return result;
    }
}
