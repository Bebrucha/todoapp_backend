package com.todoapp.todoapp.unit.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todoapp.todoapp.business.mapper.TaskMapper;

import com.todoapp.todoapp.business.persistence.TaskRepository;

import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;

import com.todoapp.todoapp.business.service.impl.TaskServiceImpl;

import com.todoapp.todoapp.model.Task;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepositoryMock;

    @Mock
    private TaskMapper taskMapperMock;

    @InjectMocks
    private TaskServiceImpl taskServiceTarget;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    Task setUpTask() {

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

    TaskDAO setUpTaskDAO() {
        return TaskDAO.builder().id(1L).title("Task 1").description("Description 1").due(LocalDate.parse("2024-12-11"))
                .statusId(1L).categoryId(1L).userId(1L).build();
    }

    Task setUpUpdateTask() {
        return Task.builder().id(1L).title("Updated task").description("Updated Description 1")
                .due(LocalDate.parse("2024-11-11"))
                .statusId(1L).categoryId(1L).userId(1L).build();
    }

    @Test
    public void testGetTaskById() {
        Task task = setUpTask();
        TaskDAO taskDAO = setUpTaskDAO();
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(taskDAO));
        when(taskMapperMock.taskDAOToTask(taskDAO)).thenReturn(task);

        Task result = taskServiceTarget.getTaskById(1L);

        Assertions.assertEquals(task, result);
        verify(taskRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void testGetAllTasks() {
        Task task = setUpTask();
        TaskDAO taskDAO = setUpTaskDAO();
        List<TaskDAO> taskDAOList = new ArrayList<>();
        taskDAOList.add(taskDAO);
        when(taskRepositoryMock.findAll()).thenReturn(taskDAOList);
        when(taskMapperMock.taskDAOToTask(taskDAO)).thenReturn(task);

        List<Task> result = taskServiceTarget.getAllTasks();

        Assertions.assertEquals(Collections.singletonList(task), result);
        verify(taskRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testSaveTask() {
        Task task = setUpTask();
        TaskDAO taskDAO = setUpTaskDAO();
        when(taskMapperMock.taskToTaskDAO(task)).thenReturn(taskDAO);
        when(taskRepositoryMock.save(taskDAO)).thenReturn(taskDAO);
        when(taskMapperMock.taskDAOToTask(taskDAO)).thenReturn(task);

        Task result = taskServiceTarget.saveTask(task);

        Assertions.assertEquals(task, result);
        verify(taskRepositoryMock, times(1)).save(taskDAO);
    }

    @Test
    public void testFindTaskById() {
        Task task = setUpTask();
        TaskDAO taskDAO = setUpTaskDAO();
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(taskDAO));
        when(taskMapperMock.taskDAOToTask(taskDAO)).thenReturn(task);

        Optional<Task> result = taskServiceTarget.findTaskById(1L);

        Assertions.assertEquals(Optional.of(task), result);
        verify(taskRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void testDeleteTaskById() {
        Task task = setUpTask();
        TaskDAO taskDAO = setUpTaskDAO();
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(taskDAO));
        when(taskMapperMock.taskDAOToTask(taskDAO)).thenReturn(task);

        Task result = taskServiceTarget.deleteTaskById(1L);

        Assertions.assertEquals(task, result);
        verify(taskRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void testTaskUpdate() {

        Task updatedTask = setUpUpdateTask();
        TaskDAO updatedTaskDAO = TaskDAO.builder().id(1L).title("Updated task").description("Updated Description 1")
                .due(LocalDate.parse("2024-11-11"))
                .statusId(1L).categoryId(1L).userId(1L).build();

        when(taskRepositoryMock.save(any(TaskDAO.class))).thenReturn(updatedTaskDAO);

        when(taskMapperMock.taskToTaskDAO(updatedTask)).thenReturn(updatedTaskDAO);
        when(taskMapperMock.taskDAOToTask(updatedTaskDAO)).thenReturn(updatedTask);

        Task result = taskServiceTarget.updateTask(updatedTask);

        Assertions.assertEquals(updatedTask, result);

        verify(taskRepositoryMock, times(1)).save(any(TaskDAO.class));
    }

}
