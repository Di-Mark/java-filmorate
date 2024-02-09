package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Getter
@Service
public class UserService {
    private final UserStorage userStorage;
    private Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public void addFriend(int id, int friendId) {
        checkUsersId(id, friendId);
        if (friends.containsKey(id)) {
            friends.get(id).add(friendId);
        } else {
            Set<Integer> tempSet = new HashSet<>();
            friends.put(id, tempSet);
            friends.get(id).add(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).add(id);
        } else {
            Set<Integer> tempSet = new HashSet<>();
            friends.put(friendId, tempSet);
            friends.get(friendId).add(id);
        }
    }

    public void deleteFriend(int id, int friendId) {
        checkUsersId(id, friendId);
        friends.get(id).remove(friendId);
        friends.get(friendId).remove(id);
    }

    public List<User> getFriendList(int id) {
        userStorage.getUser(id);
        List<User> result = new ArrayList<>();
        for (Integer tempId : friends.get(id)) {
            result.add(userStorage.getUser(tempId));
        }
        return result;
    }

    public List<User> getFriendListCommonOtherUser(int id, int otherId) {
        checkUsersId(id, otherId);
        List<User> result = new ArrayList();
        if (!friends.containsKey(id) || !friends.containsKey(otherId)) {
            return List.of();
        }
        if (friends.get(id).size() > friends.get(otherId).size()) {
            for (Integer tempId : friends.get(id)) {
                if (friends.get(otherId).contains(tempId)) {
                    result.add(userStorage.getUser(tempId));
                }
            }
        } else {
            for (Integer tempId : friends.get(otherId)) {
                if (friends.get(id).contains(tempId)) {
                    result.add(userStorage.getUser(tempId));
                }
            }
        }
        return result;
    }

    private void checkUsersId(int id, int friendId) {
        userStorage.getUser(id);
        userStorage.getUser(friendId);
    }
}
