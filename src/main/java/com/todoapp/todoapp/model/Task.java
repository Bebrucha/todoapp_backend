package com.todoapp.todoapp.model;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Task model")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Task {

    @ApiModelProperty(notes = "Task id", example = "Positive number: 1, 2")
    private Long id;

    @ApiModelProperty(notes = "Task title", example = "Learning", required = true)
    @NotNull(message = "Title is required")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @ApiModelProperty(notes = "Task description", example = "I need to learn page 20 of mathematics", required = true)
    @NotNull(message = "Description is required")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @ApiModelProperty(notes = "Due date", example = "2024-12-12", required = true)
    @NotNull(message = "Due date is required")
    private LocalDate due;

    @ApiModelProperty(notes = "Task status id", example = "Completed = 3, In progress = 2, To be completed = 1", required = true)
    @NotNull(message = "Status is required")
    private Long statusId;

    @ApiModelProperty(notes = "Category id", example = "Positive number: 1, 2", required = true)
    @NotNull(message = "Category is required")
    private Long categoryId;

    @ApiModelProperty(notes = "User id", example = "Positive number: 1, 2", required = true)
    @NotNull(message = "User is required")
    private Long userId;

}
