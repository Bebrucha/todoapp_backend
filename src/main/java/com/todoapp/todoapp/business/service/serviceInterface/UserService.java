package com.todoapp.todoapp.business.service.serviceInterface;

import java.util.List;
import java.util.Optional;

import com.todoapp.todoapp.model.User;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User saveUser(User user);

    Optional<User> findUserById(Long id);

    User deleteUserById(Long id);

    User updateUser(User user);
}
