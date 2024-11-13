package com.easy.base.service;

import com.easy.base.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User createUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(String id);
    User updateUser(User user);
    void deleteUser(String id);
}
