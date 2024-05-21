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
import com.todoapp.todoapp.business.persistence.TaskRepository;

import com.todoapp.todoapp.business.service.serviceInterface.TaskService;

import com.todoapp.todoapp.model.Task;
import com.todoapp.todoapp.security.JwtAuthenticationFilter;
import com.todoapp.todoapp.web.controller.TaskController;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {
        public static String URL = "/api/v1/";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskService taskService;

        @MockBean
        JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private TaskRepository taskRepository;

        @MockBean
        BindingResult bindingResultMock;

        @InjectMocks
        private TaskController taskControllerTarget;

        Task setUpCreateTask() {
                return Task.builder()
                                .id(1L)
                                .title("Task 1")
                                .description("Description 1")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();
        }

        @Test

        void testGetAllTasks() throws Exception {
                Task task = setUpCreateTask();
                List<Task> taskList = new ArrayList<>();
                taskList.add(task);
                when(taskService.getAllTasks()).thenReturn(taskList);

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "tasks")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetAllTasksEmpty() throws Exception {
                List<Task> taskList = new ArrayList<>();
                when(taskService.getAllTasks()).thenReturn(taskList);

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "tasks")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetTaskById() throws Exception {
                Task task = setUpCreateTask();
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(task));

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Task 1"))
                                .andExpect(jsonPath("$.description").value("Description 1"))
                                .andExpect(jsonPath("$.due").value("2024-12-11"))
                                .andExpect(jsonPath("$.statusId").value(1L))
                                .andExpect(jsonPath("$.categoryId").value(1L))
                                .andExpect(jsonPath("$.userId").value(1L));

                verify(taskService, times(1)).findTaskById(1L);
        }

        @Test
        void testGetTaskByNegativeId() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get(URL + "task/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(taskService);
        }

        @Test
        void testGetTaskByIdNotFound() throws Exception {
                when(taskService.findTaskById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(MockMvcRequestBuilders.get(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(taskService, times(1)).findTaskById(1L);
        }

        @Test
        void testTaskSaving() throws Exception {
                Task taskToSave = Task.builder()
                                .id(null)
                                .title("Task 1")
                                .description("Description 1")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();

                Task savedTask = new Task(1L, taskToSave.getTitle(), taskToSave.getDescription(), taskToSave.getDue(),
                                taskToSave.getStatusId(), taskToSave.getCategoryId(), taskToSave.getUserId());

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.saveTask(taskToSave)).thenReturn(savedTask);

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(URL + "task")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskToSave)));

                result.andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Task 1"))
                                .andExpect(jsonPath("$.description").value("Description 1"))
                                .andExpect(jsonPath("$.due").value("2024-12-11"))
                                .andExpect(jsonPath("$.statusId").value(1L))
                                .andExpect(jsonPath("$.categoryId").value(1L))
                                .andExpect(jsonPath("$.userId").value(1L));

                verify(taskService, times(1)).saveTask(any(Task.class));
        }

        @Test
        void testSavingTestValidationErrors() throws Exception {
                Task taskToSave = Task.builder().id(null).title(null).description("").build();
                when(bindingResultMock.hasErrors()).thenReturn(true);

                mockMvc.perform(MockMvcRequestBuilders.post(URL + "task")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskToSave)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).saveTask(any(Task.class));
        }

        @Test
        void testTaskDeletion() throws Exception {
                Task task = setUpCreateTask();
                when(taskService.findTaskById(anyLong()))
                                .thenReturn(Optional.of(task));

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(taskService, times(1)).deleteTaskById(1L);
        }

        @Test
        void testTaskDeletionWithNegativeId() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "task/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).deleteTaskById(anyLong());
        }

        @Test
        void testTaskDeletionTaskNotFound() throws Exception {
                when(taskService.findTaskById(anyLong()))
                                .thenReturn(Optional.empty());

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(taskService, never()).deleteTaskById(1L);
        }

        @Test
        void testTaskUpdate() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder()
                                .id(1L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));
                when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Task 1 updated"))
                                .andExpect(jsonPath("$.description").value("Description 1 updated"));

                verify(taskService, times(1)).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateValidationErrors() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder().id(1L).title(null).description("").build();

                when(bindingResultMock.hasErrors()).thenReturn(true);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateWithNegativeId() throws Exception {
                when(bindingResultMock.hasErrors()).thenReturn(false);
                mockMvc.perform(put(URL + "task/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(setUpCreateTask())))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(any(Task.class));
        }

        @Test
        void testTaskUpdateWithNegativeCategoryId() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder()
                                .id(1L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(-1L)
                                .userId(1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateWithNegativeStatusId() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder()
                                .id(1L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(-1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateWithNegativeUserId() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder()
                                .id(1L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(-1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateNotMatchingBodyAndParameterId() throws Exception {
                Task existingTask = setUpCreateTask();
                Task updatedTask = Task.builder()
                                .id(2L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.of(existingTask));

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).updateTask(updatedTask);
        }

        @Test
        void testTaskUpdateNotFound() throws Exception {
                Task updatedTask = Task.builder()
                                .id(1L)
                                .title("Task 1 updated")
                                .description("Description 1 updated")
                                .due(LocalDate.parse("2024-12-11"))
                                .statusId(1L)
                                .categoryId(1L)
                                .userId(1L)
                                .build();

                when(bindingResultMock.hasErrors()).thenReturn(false);
                when(taskService.findTaskById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(put(URL + "task/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTask)))
                                .andExpect(status().isNotFound());

                verify(taskService, never()).updateTask(updatedTask);
        }

}
