package com.todoapp.todoapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.todoapp.todoapp.business.persistence.UserRepository;
import com.todoapp.todoapp.business.persistence.DAO.UserDAO;

import java.util.Arrays;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetUserById() throws Exception {
        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Testing"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserDAO expectedUser1 = new UserDAO(1L, "Testing", "GoodPassword");
        UserDAO expectedUser2 = new UserDAO(2L, "Testing2", "GoodPassword2");
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(expectedUser1, expectedUser2));

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Testing"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Testing2"));

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"username\": \"Testing\", \"password\": \"GoodPassword\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Testing"));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateUser() throws Exception {

        UserDAO user = new UserDAO(1L, "Testing", "GoodPassword");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDAO updatedUser = new UserDAO(1L, "Working", "newPassword");
        Mockito.when(userRepository.save(Mockito.any(UserDAO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"username\": \"Working\", \"password\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Working"))
                .andExpect(jsonPath("$.password").value("newPassword"));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserDAO.class));
    }

}
