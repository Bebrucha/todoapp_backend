package com.todoapp.todoapp.business.service.serviceInterface;

import java.util.List;
import java.util.Optional;

import com.todoapp.todoapp.model.Task;

public interface TaskService {

    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task saveTask(Task task);

    Optional<Task> findTaskById(Long id);

    Task deleteTaskById(Long id);

    Task updateTask(Task user);

}