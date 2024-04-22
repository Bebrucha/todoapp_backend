package com.todoapp.todoapp.web.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.todoapp.todoapp.business.service.serviceInterface.TaskService;

import com.todoapp.todoapp.model.Task;

import com.todoapp.todoapp.swagger.DescriptionVariables;
import com.todoapp.todoapp.swagger.HTMLResponseMessages;

import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Api(tags = { DescriptionVariables.Task })
@Log4j2
@RestController
@RequestMapping("/api/v1")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Retrieves all tasks from the system.
     *
     * @return A ResponseEntity containing the task if found, or an appropriate
     *         error response if not found.
     */
    @GetMapping("/tasks")
    @ApiOperation(value = "Get all tasks", response = Task.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("Received request to get all tasks");

        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            log.warn("No tasks found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        log.info("Returning all tasks");
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return A ResponseEntity containing the task if found, or an appropriate
     *         error response if not found.
     */
    @GetMapping("/task/{id}")
    @ApiOperation(value = "Get a task by id", response = Task.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<Task> getTaskById(@ApiParam(value = "id", required = true) @NonNull @PathVariable Long id) {
        log.info("Received request to get task with id {}", id);

        if (id <= 0) {
            log.warn("Task id should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Task> task = taskService.findTaskById(id);
        if (!task.isPresent()) {
            log.warn("Task with id {} is not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Returning task with id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(task.get());
    }

    /**
     * Creates a new task.
     *
     * @param task          The task object to be created.
     * @param bindingResult The result of the validation process.
     * @return The response entity containing the created task or error message.
     */
    @PostMapping("/task")
    @ApiOperation(value = "Create a new task", response = Task.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<Object> createTask(@Valid @RequestBody Task task, BindingResult bindingResult) {

        log.info("Received request to save a new task {}", task);

        if (bindingResult.hasErrors()) {
            log.error("Task validation failed: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Task savedTask = taskService.saveTask(task);

        if (savedTask != null) {
            log.info("Task saved successfully {}", savedTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the task");
        }

    }

    /**
     * Deletes a task by id.
     *
     * @param id the id of the task to be deleted
     * @return a ResponseEntity with a status code indicating the result of the
     *         deletion
     */
    @DeleteMapping("/task/{id}")
    @ApiOperation(value = "Deletes a task by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITHOUT_DATA),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTask(@ApiParam(value = "id", required = true) @PathVariable Long id) {
        log.info("Received request to delete task with id {}", id);

        if (id <= 0) {
            log.warn("Task id for delete should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Task> task = taskService.findTaskById(id);
        if (!task.isPresent()) {
            log.warn("Task with id {} is not found for delete operation", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        taskService.deleteTaskById(id);
        log.info("Task with id {} has been deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Updates a task by its ID.
     *
     * @param id            The ID of the task to be updated.
     * @param task          The updated task object.
     * @param bindingResult The result of the validation process.
     * @return A ResponseEntity containing the updated task object if successful, or
     *         an error response if not.
     */
    @PutMapping("/task/{id}")
    @ApiOperation(value = "Update a task by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500),
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateTask(@ApiParam(value = "id", required = true) @PathVariable Long id,
            @Valid @RequestBody Task task, BindingResult bindingResult) {
        log.info("Received request to update task with id {}", id);

        if (bindingResult.hasErrors()) {
            log.error("Task validation failed: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        if (id <= 0) {
            log.warn("Task id for update should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (task.getStatusId() <= 0) {
            log.warn("Task status id for update should be greater then zero. It is {}", task.getStatusId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (task.getCategoryId() <= 0) {
            log.warn("Task category id for update should be greater then zero. It is {}", task.getCategoryId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (task.getUserId() <= 0) {
            log.warn("Task user id for update should be greater then zero. It is {}", task.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (task.getId() != id) {
            log.warn("The id in the path and the id in the body do not match: parameter id {} and body id {}", id,
                    task.getId());
            return ResponseEntity.badRequest().build();
        }

        Optional<Task> taskOptional = taskService.findTaskById(id);
        if (!taskOptional.isPresent()) {
            log.warn("Task with id {} is not found for update operation", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Task updatedTask = taskService.updateTask(task);
        log.info("Task with id {} has been updated", id);

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
