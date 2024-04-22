package com.todoapp.todoapp.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "User model")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    @ApiModelProperty(notes = "User id", example = "Positive number: 1, 2")
    private Long id;

    @ApiModelProperty(notes = "Username", example = "Racer853", required = true)
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @ApiModelProperty(notes = "User password", example = "strongPass1@0", required = true)
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
