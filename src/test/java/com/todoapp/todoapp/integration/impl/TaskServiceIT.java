package com.todoapp.todoapp.integration.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.todoapp.todoapp.TodoappApplication;
import com.todoapp.todoapp.business.persistence.TaskRepository;

import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;

import com.todoapp.todoapp.business.service.serviceInterface.TaskService;

import com.todoapp.todoapp.model.Task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = TodoappApplication.class)
@ExtendWith(SpringExtension.class)
public class TaskServiceIT {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    public void testGetAllTasks() {

        TaskDAO expectedTask1 = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        TaskDAO expectedTask2 = new TaskDAO(2L, "Learning!!", "IsAHardProcess!!", LocalDate.of(2024, 12, 12), 1L, 1L,
                1L);
        List<TaskDAO> expectedTasks = Arrays.asList(expectedTask1, expectedTask2);
        given(taskRepository.findAll()).willReturn(expectedTasks);

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo(expectedTask1.getTitle());
        assertThat(result.get(1).getTitle()).isEqualTo(expectedTask2.getTitle());
    }

    @Test
    public void getTaskById() {

        TaskDAO expectedTaskDAO = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        Task expectedTask = new Task(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        given(taskRepository.findById(1L)).willReturn(Optional.of(expectedTaskDAO));

        Task result = taskService.getTaskById(1L);

        assertThat(result).isEqualTo(expectedTask);
    }

    @Test
    public void saveTask() {
        Task task = new Task(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        TaskDAO taskDAO = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        given(taskRepository.save(any(TaskDAO.class))).willReturn(taskDAO);

        Task result = taskService.saveTask(task);

        assertThat(result).isEqualTo(task);
    }

    @Test
    public void deleteTaskById() {
        Task task = new Task(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        TaskDAO taskDAO = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        given(taskRepository.save(any(TaskDAO.class))).willReturn(taskDAO);

        Task result = taskService.saveTask(task);

        assertThat(result).isEqualTo(task);
    }

    @Test
    public void updateTask() {

        Task task = new Task(1L, "UpdatedTitle", "UpdatedDescription", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        TaskDAO taskDAO = new TaskDAO(1L, "Learning", "IsAHardProcess", LocalDate.of(2024, 12, 12), 1L, 1L, 1L);
        TaskDAO updatedTaskDAO = new TaskDAO(1L, "UpdatedTitle", "UpdatedDescription", LocalDate.of(2024, 12, 12), 1L,
                1L, 1L);
        given(taskRepository.findById(1L)).willReturn(Optional.of(taskDAO));
        given(taskRepository.save(any(TaskDAO.class))).willReturn(updatedTaskDAO);

        Task result = taskService.updateTask(task);

        assertThat(result.getTitle()).isEqualTo("UpdatedTitle");
        assertThat(result.getDescription()).isEqualTo("UpdatedDescription");
    }

}
