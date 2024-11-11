package com.easy.base.service;

import com.easy.base.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    public User createUser(User user);

    public List<User> getAllUsers();

    public User getUserById(String id);

    public User updateUser(User user);

    public void deleteUser(String id);
}
