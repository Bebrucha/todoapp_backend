package com.todoapp.todoapp.business.mapper;

import org.mapstruct.Mapper;

import com.todoapp.todoapp.business.persistence.DAO.UserDAO;
import com.todoapp.todoapp.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDAO userToUserDAO(User user);

    User userDAOToUser(UserDAO userDAO);
}