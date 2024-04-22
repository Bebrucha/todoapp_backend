package com.todoapp.todoapp.integration.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.todoapp.todoapp.TodoappApplication;
import com.todoapp.todoapp.business.persistence.UserRepository;
import com.todoapp.todoapp.business.persistence.DAO.UserDAO;
import com.todoapp.todoapp.business.service.serviceInterface.UserService;
import com.todoapp.todoapp.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TodoappApplication.class)
@ExtendWith(SpringExtension.class)
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetUserById() {

        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        User expectedUser = new User(1L, "Testing", "GoodPassword");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    public void testGetAllUsers() {

        UserDAO expectedUser1 = new UserDAO(1L, "Testing", "GoodPassword");
        UserDAO expectedUser2 = new UserDAO(2L, "Testing2", "GoodPassword2");
        List<UserDAO> expectedUsers = Arrays.asList(expectedUser1, expectedUser2);
        given(userRepository.findAll()).willReturn(expectedUsers);

        List<User> expectedUserList = expectedUsers.stream()
                .map(userDAO -> new User(userDAO.getId(), userDAO.getUsername(), userDAO.getPassword()))
                .collect(Collectors.toList());

        assertThat(userService.getAllUsers()).containsExactlyInAnyOrderElementsOf(expectedUserList);
    }

    @Test
    public void testSaveUser() {

        User user = new User(1L, "Testing", "GoodPassword");
        UserDAO userDAO = new UserDAO(1L, "Testing", "GoodPassword");
        given(userRepository.save(userDAO)).willReturn(userDAO);

        User result = userService.saveUser(user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    public void findUserById() {
        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        User expectedUser = new User(1L, "Testing", "GoodPassword");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    public void deleteUserById() {
        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void updateUser() {
        User user = new User(1L, "Testing", "UpdatedPassword");
        UserDAO userDAO = new UserDAO(1L, "Testing", "UpdatedPassword");
        UserDAO existingUserDAO = new UserDAO(1L, "Testing", "GoodPassword");
        given(userRepository.findById(1L)).willReturn(Optional.of(existingUserDAO));
        given(userRepository.save(any())).willReturn(userDAO);

        User result = userService.updateUser(user);

        assertThat(result.getPassword()).isEqualTo("UpdatedPassword");
    }

}
