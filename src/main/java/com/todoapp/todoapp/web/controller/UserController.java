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

import com.todoapp.todoapp.business.persistence.DAO.UserDAO;
import com.todoapp.todoapp.business.service.serviceInterface.UserService;
import com.todoapp.todoapp.model.User;
import com.todoapp.todoapp.swagger.DescriptionVariables;
import com.todoapp.todoapp.swagger.HTMLResponseMessages;

import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Api(tags = { DescriptionVariables.User })
@Log4j2
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves all users.
     *
     * @return A ResponseEntity containing a list of all users if successful, or
     *         an appropriate error response if unsuccessful.
     */
    @GetMapping("/users")
    @ApiOperation(value = "Get all users", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Received request to get all users");

        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            log.warn("No users found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        log.info("Returning all users");
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the user if found, or an appropriate
     *         error response if not found.
     */
    @GetMapping("/user/{id}")
    @ApiOperation(value = "Get a user by id", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<User> getUserById(@ApiParam(value = "id", required = true) @NonNull @PathVariable Long id) {
        log.info("Received request to get user with id {}", id);

        if (id <= 0) {
            log.warn("User id should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<User> user = userService.findUserById(id);
        if (!user.isPresent()) {
            log.warn("User with id {} is not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Returning user with id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

    /**
     * Creates a new user.
     *
     * @param user          The user object to be created.
     * @param bindingResult The result of the validation process.
     * @return A ResponseEntity containing the created user object if successful, or
     *         an error message if unsuccessful.
     */
    @PostMapping("/user")
    @ApiOperation(value = "Create a new user", response = UserDAO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user, BindingResult bindingResult)

    {
        log.info("Received request to save a new user {}", user);

        if (bindingResult.hasErrors()) {
            log.error("User validation failed: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        User savedUser = userService.saveUser(user);

        if (savedUser != null) {
            log.info("User saved successfully {}", savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the user");
        }

    }

    /**
     * Deletes a user by id.
     *
     * @param id the id of the user to be deleted
     * @return a ResponseEntity with a void body
     */
    @DeleteMapping("/user/{id}")
    @ApiOperation(value = "Deletes a user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITHOUT_DATA),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500) })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@ApiParam(value = "id", required = true) @PathVariable Long id) {
        log.info("Received request to delete user with id {}", id);

        if (id <= 0) {
            log.warn("User id for delete should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<User> user = userService.findUserById(id);
        if (!user.isPresent()) {
            log.warn("User with id {} is not found for delete operation", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        userService.deleteUserById(id);
        log.info("User with id {} has been deleted", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Updates a user by ID.
     *
     * @param id            The ID of the user to update.
     * @param user          The updated user object.
     * @param bindingResult The result of the validation process.
     * @return A ResponseEntity containing the updated user object if successful, or
     *         an error response if not.
     */
    @PutMapping("/user/{id}")
    @ApiOperation(value = "Update a user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500),
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@ApiParam(value = "id", required = true) @PathVariable Long id,
            @Valid @RequestBody User user, BindingResult bindingResult) {
        log.info("Received request to update user with id {}", id);

        if (bindingResult.hasErrors()) {
            log.error("User validation failed: {}", bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        if (id <= 0) {
            log.warn("User id for update should be greater then zero. It is {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (user.getId() != id) {
            log.warn("The id in the path and the id in the body do not match: parameter id {} and body id {}", id,
                    user.getId());
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOptional = userService.findUserById(id);
        if (!userOptional.isPresent()) {
            log.warn("User with id {} is not found for update operation", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User updatedUser = userService.updateUser(user);
        log.info("User with id {} has been updated", id);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

}
