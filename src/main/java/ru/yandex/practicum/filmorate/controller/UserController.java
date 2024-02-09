package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@Getter
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userService.getUserStorage().findAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.getUserStorage().createUser(user);
    }

    @PutMapping("/users")
    public User patchUser(@RequestBody User user) {
        return userService.getUserStorage().patchUser(user);
    }

    @GetMapping("/users/{id}")
    public User findUser(@PathVariable("id") Integer id) {
        return userService.getUserStorage().getUser(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable Integer id) {
        return userService.getFriendList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getFriendListCommonOtherUser(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getFriendListCommonOtherUser(id, otherId);
    }
}
