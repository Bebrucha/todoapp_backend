package com.todoapp.todoapp.unit.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todoapp.todoapp.business.mapper.UserMapper;
import com.todoapp.todoapp.business.persistence.UserRepository;
import com.todoapp.todoapp.business.persistence.DAO.UserDAO;
import com.todoapp.todoapp.business.service.impl.UserServiceImpl;
import com.todoapp.todoapp.model.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @InjectMocks
    private UserServiceImpl userServiceTarget;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    User setUpUser() {
        return User.builder().id(1L).username("test").password("test").build();
    }

    UserDAO setUpUserDAO() {
        return UserDAO.builder().id(1L).username("test").password("test").build();
    }

    User setUpUpdateUser() {
        return User.builder().id(1L).username("placeholder").password("placeholder").build();
    }

    @Test
    public void testGetUserById() {
        User user = setUpUser();
        UserDAO userDAO = setUpUserDAO();
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userMapperMock.userDAOToUser(userDAO)).thenReturn(user);

        User result = userServiceTarget.getUserById(1L);

        Assertions.assertEquals(user, result);
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void testGetAllUsers() {
        User user = setUpUser();
        UserDAO userDAO = setUpUserDAO();
        List<UserDAO> userDAOList = new ArrayList<>();
        userDAOList.add(userDAO);
        when(userRepositoryMock.findAll()).thenReturn(userDAOList);
        when(userMapperMock.userDAOToUser(userDAO)).thenReturn(user);

        List<User> result = userServiceTarget.getAllUsers();

        Assertions.assertEquals(Collections.singletonList(user), result);
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testSaveUser() {
        User user = setUpUser();
        UserDAO userDAO = setUpUserDAO();
        when(userMapperMock.userToUserDAO(user)).thenReturn(userDAO);
        when(userRepositoryMock.save(userDAO)).thenReturn(userDAO);
        when(userMapperMock.userDAOToUser(userDAO)).thenReturn(user);

        User result = userServiceTarget.saveUser(user);

        Assertions.assertEquals(user, result);
        verify(userRepositoryMock, times(1)).save(userDAO);
    }

    @Test
    public void testFindUserById() {
        User user = setUpUser();
        UserDAO userDAO = setUpUserDAO();
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userMapperMock.userDAOToUser(userDAO)).thenReturn(user);

        Optional<User> result = userServiceTarget.findUserById(1L);

        Assertions.assertEquals(Optional.of(user), result);
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void testDeleteUserById() {
        User user = setUpUser();
        UserDAO userDAO = setUpUserDAO();
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(userDAO));
        when(userMapperMock.userDAOToUser(userDAO)).thenReturn(user);

        User result = userServiceTarget.deleteUserById(1L);

        Assertions.assertEquals(user, result);
        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void testUserUpdate() {

        User updatedUser = setUpUpdateUser();
        UserDAO updatedUserDAO = UserDAO.builder().id(1L).username("testPassed").password("testPassed").build();

        when(userRepositoryMock.save(any(UserDAO.class))).thenReturn(updatedUserDAO);

        when(userMapperMock.userToUserDAO(updatedUser)).thenReturn(updatedUserDAO);
        when(userMapperMock.userDAOToUser(updatedUserDAO)).thenReturn(updatedUser);

        User result = userServiceTarget.updateUser(updatedUser);

        Assertions.assertEquals(updatedUser, result);

        verify(userRepositoryMock, times(1)).save(any(UserDAO.class));
    }

}
