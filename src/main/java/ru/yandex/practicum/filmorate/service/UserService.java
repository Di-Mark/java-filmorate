package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Getter
@Service
public class UserService {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(int id, int friendId) {
        checkUsersId(id, friendId);
        String sql1 = "select user_id from friendship where user_id = ? and friend_id = ?";
        String sqlInsert = "insert into friendship (user_id, friend_id, status) values (?, ?, ?)";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql1, friendId, id);
        if (sqlRowSet.next()) {
            jdbcTemplate.update(sqlInsert, id, friendId, true);
            String sqlUpdate = "update friendship set status = ? where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sqlUpdate, true, friendId, id);
        } else {
            jdbcTemplate.update(sqlInsert, id, friendId, false);
        }
    }

    public void deleteFriend(int id, int friendId) {
        checkUsersId(id, friendId);
        String sqlDelete = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlDelete, id, friendId);
    }

    public List<User> getFriendList(int id) {
        userStorage.getUser(id);
        String sql = "select * from users where user_id in" +
                "(select friend_id from friendship where user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    public List<User> getFriendListCommonOtherUser(int id, int otherId) {
        checkUsersId(id, otherId);
        List<User> result = new ArrayList();
        List<User> idFriendList = getFriendList(id);
        List<User> otherIdList = getFriendList(otherId);
        for (User tempUser : idFriendList) {
            if (otherIdList.contains(tempUser)) {
                result.add(tempUser);
            }
        }
        return result;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    private void checkUsersId(int id, int friendId) {
        userStorage.getUser(id);
        userStorage.getUser(friendId);
    }
}
