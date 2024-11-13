package com.easy.base.service.impl;

import com.easy.base.exception.user.DuplicateEmailException;
import com.easy.base.exception.user.DuplicateUsernameException;
import com.easy.base.exception.user.InvalidUserIdException;
import com.easy.base.exception.user.UserNotFoundException;
import com.easy.base.model.User;
import com.easy.base.repository.UserRepository;
import com.easy.base.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserService self;

    public UserServiceImpl(
            UserRepository userRepository,
            @Lazy UserService self) {
        this.userRepository = userRepository;
        this.self = self;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        _validateUniqueConstraints(user, null);

        user.setCreatedDate(new Date());
        user.setModifiedDate(new Date());
        user.setActive(true);
        try {
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid user data provided", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(String id) {
        try {
            return userRepository.findById(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid user ID format: " + id, e);
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            User existingUser = self.getUserById(user.getId().toString())
                    .orElseThrow(() -> new UserNotFoundException(
                            "User not found with id: " + user.getId()));

            _validateUniqueConstraints(user, existingUser.getId().toString());

            user.setModifiedDate(new Date());
            user.setCreatedDate(existingUser.getCreatedDate());
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid user ID format", e);
        }
    }

    @Override
    public void deleteUser(String id) {
        try {
                Optional<User> user = self.getUserById(id);  // Use self instead of this
            if (user.isEmpty()) {
                throw new UserNotFoundException("User not found with id: " + id);
            }
            userRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid user ID format: " + id, e);
        }
    }

    private void _validateUniqueConstraints(User user, String excludeId) {
        // Check username uniqueness
        userRepository.findByUsername(user.getUsername())
                .filter(u -> excludeId == null || !u.getId().toString().equals(excludeId))
                .ifPresent(u -> {
                    throw new DuplicateUsernameException(
                            "Username already exists: " + user.getUsername());
                });

        // Check email uniqueness
        userRepository.findByEmail(user.getEmail())
                .filter(u -> excludeId == null || !u.getId().toString().equals(excludeId))
                .ifPresent(u -> {
                    throw new DuplicateEmailException(
                            "Email already exists: " + user.getEmail());
                });
    }
}
