package com.todoapp.todoapp.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.todoapp.todoapp.dto.AuthenticationRequest;
import com.todoapp.todoapp.security.auth.AuthenticationResponse;
import com.todoapp.todoapp.security.auth.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * Authenticates the user with the provided authentication request.
     *
     * @param request the authentication request containing the user's credentials
     * @return a ResponseEntity containing the authentication JWT token
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
