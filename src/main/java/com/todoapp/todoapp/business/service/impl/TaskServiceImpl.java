package com.todoapp.todoapp.business.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todoapp.todoapp.business.mapper.TaskMapper;
import com.todoapp.todoapp.business.persistence.TaskRepository;
import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;
import com.todoapp.todoapp.business.service.serviceInterface.TaskService;
import com.todoapp.todoapp.model.Task;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapper taskMapper;

    /**
     * Retrieves all tasks from the task repository.
     *
     * @return a list of all tasks
     */
    @Override
    public List<Task> getAllTasks() {
        log.info("Getting all tasks");
        List<TaskDAO> taskDAOList = taskRepository.findAll();
        return taskDAOList.stream().map(taskMapper::taskDAOToTask).collect(Collectors.toList());
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task to retrieve
     * @return the task with the specified ID, or null if not found
     */
    @Override
    public Task getTaskById(Long id) {
        log.info("Getting task by id {}", id);
        return taskMapper.taskDAOToTask(taskRepository.findById(id).get());
    }

    /**
     * Saves a task.
     *
     * @param task The task to be saved.
     * @return The saved task.
     */
    @Override
    public Task saveTask(Task task) {
        log.info("Saving task: {}", task.getTitle());
        TaskDAO taskDAO = taskMapper.taskToTaskDAO(task);
        return taskMapper.taskDAOToTask(taskRepository.save(taskDAO));
    }

    /**
     * Finds a task by its ID.
     *
     * @param id the ID of the task to find
     * @return an Optional containing the task if found, or an empty Optional if not
     *         found
     */
    @Override
    public Optional<Task> findTaskById(Long id) {
        log.info("Finding task by id {}", id);
        return taskRepository.findById(id).map(taskMapper::taskDAOToTask);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to be deleted
     * @return the deleted task
     */
    @Override
    public Task deleteTaskById(Long id) {
        log.info("Deleting task by id {}", id);
        TaskDAO taskDAO = taskRepository.findById(id).get();
        taskRepository.deleteById(id);
        return taskMapper.taskDAOToTask(taskDAO);
    }

    /**
     * Updates a task.
     *
     * @param user The task to be updated.
     * @return The updated task.
     */
    @Override
    public Task updateTask(Task user) {
        log.info("Updating task: {}", user.getTitle());
        TaskDAO taskDAO = taskMapper.taskToTaskDAO(user);
        return taskMapper.taskDAOToTask(taskRepository.save(taskDAO));
    }
}
