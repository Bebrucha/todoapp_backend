package com.todoapp.todoapp.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.todoapp.todoapp.dto.AuthenticationRequest;

import com.todoapp.todoapp.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userService;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user based on the provided authentication request.
     *
     * @param request the authentication request containing the username and
     *                password
     * @return the authentication response containing the generated JWT token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails user = userService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
