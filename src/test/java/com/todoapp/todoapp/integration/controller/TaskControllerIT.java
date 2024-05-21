package com.todoapp.todoapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.todoapp.todoapp.business.persistence.TaskRepository;

import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskRepository taskRepository;

        @Test
        @WithMockUser
        public void testGetTaskById() throws Exception {
                TaskDAO task = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
                Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

                mockMvc.perform(get("/api/v1/task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Learning"));

                Mockito.verify(taskRepository, Mockito.times(1)).findById(1L);
        }

        @Test
        @WithMockUser
        public void testGetAllTasks() throws Exception {
                TaskDAO expectedTask1 = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L,
                                1L, 1L);
                TaskDAO expectedTask2 = new TaskDAO(2L, "Learning!!", "IsAHardProcess!!", LocalDate.of(2024, 12, 12),
                                1L, 1L,
                                1L);
                Mockito.when(taskRepository.findAll()).thenReturn(Arrays.asList(expectedTask1, expectedTask2));

                mockMvc.perform(get("/api/v1/tasks")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].title").value("Learning"))
                                .andExpect(jsonPath("$[1].id").value(2L))
                                .andExpect(jsonPath("$[1].title").value("Learning!!"));

                Mockito.verify(taskRepository, Mockito.times(1)).findAll();
        }

        @Test
        @WithMockUser
        public void testCreateTask() throws Exception {
                TaskDAO task = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
                Mockito.when(taskRepository.save(Mockito.any(TaskDAO.class))).thenReturn(task);

                mockMvc.perform(post("/api/v1/task")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                                "{\"id\": 1, \"title\": \"Learning\", \"description\": \"IsAHardProcess\", \"due\": \"2024-12-12\", \"userId\": 1, \"categoryId\": 1, \"statusId\": 1}"))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Learning"));

                Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any(TaskDAO.class));
        }

        @Test
        @WithMockUser
        public void testDeleteTask() throws Exception {
                TaskDAO task = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
                Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

                mockMvc.perform(delete("/api/v1/task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                Mockito.verify(taskRepository, Mockito.times(1)).deleteById(1L);
        }

        @Test
        @WithMockUser
        public void testUpdateTask() throws Exception {

                TaskDAO existingTask = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L,
                                1L);

                Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

                TaskDAO updatedTask = new TaskDAO(1L, "Updated Learning", "Updated IsAHardProcess",
                                LocalDate.of(2025, 1, 1),
                                2L, 2L, 2L);

                Mockito.when(taskRepository.save(Mockito.any(TaskDAO.class))).thenReturn(updatedTask);

                mockMvc.perform(put("/api/v1/task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                                "{\"id\": 1, \"title\": \"Updated Learning\", \"description\": \"Updated IsAHardProcess\", \"due\": \"2025-01-01\", \"userId\": 2, \"categoryId\": 2, \"statusId\": 2}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Updated Learning"))
                                .andExpect(jsonPath("$.description").value("Updated IsAHardProcess"))
                                .andExpect(jsonPath("$.due").value("2025-01-01"))
                                .andExpect(jsonPath("$.userId").value(2L))
                                .andExpect(jsonPath("$.categoryId").value(2L))
                                .andExpect(jsonPath("$.statusId").value(2L));

                Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any(TaskDAO.class));
        }

}
