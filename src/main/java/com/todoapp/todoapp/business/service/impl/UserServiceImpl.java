package com.todoapp.todoapp.business.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todoapp.todoapp.business.mapper.UserMapper;
import com.todoapp.todoapp.business.persistence.UserRepository;
import com.todoapp.todoapp.business.persistence.DAO.UserDAO;
import com.todoapp.todoapp.business.service.serviceInterface.UserService;
import com.todoapp.todoapp.model.User;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID, or null if no user is found
     */
    @Override
    public User getUserById(Long id) {
        log.info("Getting user by id {}", id);
        return userMapper.userDAOToUser(userRepository.findById(id).get());
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a list of User objects representing all users
     */
    @Override
    public List<User> getAllUsers() {
        log.info("Getting all users");
        List<UserDAO> userDAOList = userRepository.findAll();
        return userDAOList.stream().map(userMapper::userDAOToUser).collect(Collectors.toList());
    }

    /**
     * Saves the user
     *
     * @param user The user object to be saved.
     * @return The saved user object.
     */
    @Override
    public User saveUser(User user) {
        log.info("Saving user: {}", user.getUsername());
        UserDAO savedUserDAO = userRepository.save(userMapper.userToUserDAO(user));
        log.info("User saved successfully {}", savedUserDAO.getUsername());
        return userMapper.userDAOToUser(savedUserDAO);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return an Optional containing the user if found, or an empty Optional if not
     *         found
     */
    @Override
    public Optional<User> findUserById(Long id) {
        log.info("Finding user by id {}", id);
        return userRepository.findById(id).map(userMapper::userDAOToUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return the deleted user
     */
    @Override
    public User deleteUserById(Long id) {
        log.info("Deleting user by id {}", id);
        UserDAO userDAO = userRepository.findById(id).get();
        userRepository.deleteById(id);
        log.info("User with id {} was deleted successfully", id);
        return userMapper.userDAOToUser(userDAO);
    }

    /**
     * Updates a user.
     *
     * @param user The user object to be updated.
     * @return The updated user object.
     */
    @Override
    public User updateUser(User user) {

        log.info("Updating user with id {}", user.getId());
        UserDAO userDAO = userRepository.save(userMapper.userToUserDAO(user));
        log.info("User with id {} was updated successfully", user.getId());
        return userMapper.userDAOToUser(userDAO);

    }

}
