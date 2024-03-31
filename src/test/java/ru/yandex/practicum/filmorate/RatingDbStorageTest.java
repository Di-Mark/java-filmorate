package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.enums.RatingName;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.util.ArrayList;
import java.util.List;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void getRatingTest() {
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        assertEquals(new Rating(1, RatingName.G), ratingDbStorage.getRating(1));
    }

    @Test
    public void findAllRatingTest() {
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        Rating[] arrRes = ratingDbStorage.findAllRating().toArray(new Rating[0]);
        List<Rating> temp = new ArrayList<>();
        temp.add(new Rating(1, RatingName.G));
        temp.add(new Rating(2, RatingName.PG));
        temp.add(new Rating(3, RatingName.PG_13));
        temp.add(new Rating(4, RatingName.R));
        temp.add(new Rating(5, RatingName.NC_17));
        Rating[] arrT = temp.toArray(new Rating[0]);
        assertArrayEquals(arrT, arrRes);
    }

    @Test
    public void getErrorRatingTest() {
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        assertThrowsExactly(NotFoundException.class, () -> ratingDbStorage.getRating(500));
    }
}
