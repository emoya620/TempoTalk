package com.project.tempotalk.services;

import com.project.tempotalk.models.ERole;
import com.project.tempotalk.models.Role;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.LoginRequest;
import com.project.tempotalk.payload.request.SignupRequest;
import com.project.tempotalk.payload.response.AuthResponse;
import com.project.tempotalk.payload.response.JwtResponse;
import com.project.tempotalk.repositories.RoleRepository;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.security.jwt.JwtUtils;
import com.project.tempotalk.security.securityServices.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Service layer for AuthController
@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    // Returns a JWT token which indicates whether a user was successfully authenticated or not
    public JwtResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    // Returns an AuthResponse indicating whether a not was successfully registered
    public AuthResponse registerUser(SignupRequest signupRequest){
        // If inputted username already exists, then don't register user
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new AuthResponse("Error: Username is already taken!");
        }

        // If inputted email already exists, then don't register user
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new AuthResponse("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        // Set a newly registered user's roles
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new AuthResponse(user,"User registered successfully!");
    }
}