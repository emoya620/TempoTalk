package com.project.tempotalk.controllers;

import com.project.tempotalk.payload.request.LoginRequest;
import com.project.tempotalk.payload.request.SignupRequest;
import com.project.tempotalk.payload.response.AuthResponse;
import com.project.tempotalk.payload.response.JwtResponse;
import com.project.tempotalk.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    // Endpoint for authenticating a user who is trying to sign in
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest){
        return new ResponseEntity<>(authService.authenticateUser(loginRequest), HttpStatus.OK);
    }

    // Endpoint for registering a new user
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignupRequest signupRequest){
        AuthResponse response = authService.registerUser(signupRequest);

        if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}