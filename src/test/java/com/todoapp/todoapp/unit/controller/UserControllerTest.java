package com.todoapp.todoapp.unit.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.todoapp.business.persistence.UserRepository;
import com.todoapp.todoapp.business.service.serviceInterface.UserService;
import com.todoapp.todoapp.model.User;
import com.todoapp.todoapp.security.JwtAuthenticationFilter;
import com.todoapp.todoapp.web.controller.UserController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.validation.BindingResult;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

        public static String URL = "/api/v1/";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        @Qualifier("userServiceImpl")
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        BindingResult bindingResultMock;

        @InjectMocks
        private UserController userControllerTarget;

        User setUpCreateUser() {
                return User.builder().id(1L).username("test").password("test").build();
        }

        @Test
        void testGetAllUsers() throws Exception {
                User user = setUpCreateUser();
                List<User> userList = new ArrayList<>();
                userList.add(user);
                when(userService.getAllUsers()).thenReturn(userList);

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetAllUsersEmpty() throws Exception {
                List<User> userList = new ArrayList<>();
                when(userService.getAllUsers()).thenReturn(userList);

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetUserById() throws Exception {
                User user = setUpCreateUser();
                when(userService.findUserById(1L)).thenReturn(Optional.of(user));

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.username").value("test"))
                                .andExpect(jsonPath("$.password").value("test"));

                verify(userService, times(1)).findUserById(1L);
        }

        @Test
        void testGetUserByNegativeId() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get(URL + "user/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(userService);
        }

        @Test
        void testGetUserByIdNotFound() throws Exception {
                when(userService.findUserById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(userService, times(1)).findUserById(1L);
        }

        @Test
        void testUserSaving() throws Exception {

                User userToSave = User.builder().id(null).username("test").password("test").build();

                User savedUser = new User(1L, userToSave.getUsername(), userToSave.getPassword());

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(userService.saveUser(userToSave)).thenReturn(savedUser);

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(URL + "user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userToSave)));

                result.andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.username").value("test"))
                                .andExpect(jsonPath("$.password").value("test"));

                verify(userService, times(1)).saveUser(any(User.class));
        }

        @Test
        void testSavingUserValidationErrors() throws Exception {
                User userToSave = User.builder().id(null).username(null).password("").build();
                when(bindingResultMock.hasErrors()).thenReturn(true);

                mockMvc.perform(MockMvcRequestBuilders.post(URL + "user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userToSave)))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).saveUser(any(User.class));
        }

        @Test
        void testUserDeletion() throws Exception {
                User user = setUpCreateUser();
                when(userService.findUserById(anyLong()))
                                .thenReturn(Optional.of(user));

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(userService, times(1)).deleteUserById(1L);
        }

        @Test
        void testUserDeletionWithNegativeId() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "user/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).deleteUserById(anyLong());
        }

        @Test
        void testUserDeletionUserNotFound() throws Exception {
                when(userService.findUserById(anyLong()))
                                .thenReturn(Optional.empty());

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(userService, never()).deleteUserById(1L);
        }

        @Test
        void testUserUpdate() throws Exception {
                User existingUser = setUpCreateUser();
                User updatedUser = User.builder().id(1L).username("testUpdate").password("testUpdate").build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(userService.findUserById(1L)).thenReturn(Optional.of(existingUser));
                when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

                mockMvc.perform(put(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.username").value("testUpdate"))
                                .andExpect(jsonPath("$.password").value("testUpdate"));

                verify(userService, times(1)).updateUser(updatedUser);
        }

        @Test
        void testUserUpdateValidationErrors() throws Exception {
                User existingUser = setUpCreateUser();
                User updatedUser = User.builder().id(1L).username(null).password("").build();

                when(bindingResultMock.hasErrors()).thenReturn(true);
                when(userService.findUserById(1L)).thenReturn(Optional.of(existingUser));

                mockMvc.perform(put(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser)))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).updateUser(updatedUser);
        }

        @Test
        void testUserUpdateWithNegativeId() throws Exception {
                when(bindingResultMock.hasErrors()).thenReturn(false);
                mockMvc.perform(put(URL + "user/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(setUpCreateUser())))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).updateUser(any(User.class));
        }

        @Test
        void testUserUpdateNotMatchingBodyAndParameterId() throws Exception {
                User existingUser = setUpCreateUser();
                User updatedUser = User.builder().id(2L).username("testUpdate").password("testUpdate").build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(userService.findUserById(1L)).thenReturn(Optional.of(existingUser));

                mockMvc.perform(put(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser)))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).updateUser(updatedUser);
        }

        @Test
        void testUserUpdateNotFound() throws Exception {
                User updatedUser = User.builder().id(1L).username("testUpdate").password("testUpdate").build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(userService.findUserById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(put(URL + "user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser)))
                                .andExpect(status().isNotFound());

                verify(userService, never()).updateUser(updatedUser);
        }

}
