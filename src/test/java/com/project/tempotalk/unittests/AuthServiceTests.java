package com.project.tempotalk.unittests;

import com.project.tempotalk.models.ERole;
import com.project.tempotalk.models.Role;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.SignupRequest;
import com.project.tempotalk.payload.response.AuthResponse;
import com.project.tempotalk.repositories.RoleRepository;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.project.tempotalk.models.ERole.ROLE_ADMIN;
import static com.project.tempotalk.models.ERole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Emilio and David
// Unit tests for the AuthService class
@ExtendWith(MockitoExtension.class)
public class AuthServiceTests{

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private User adminUser;
    private Optional<Role> userRole;
    private Optional<Role> adminRole;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        userRole = Optional.of(new Role(ROLE_USER));
        adminRole = Optional.of(new Role(ROLE_ADMIN));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ROLE_USER));

        user = new User("test", "test@gmail.com", "password");
        user.setRoles(roles);

        roles.clear();

        roles.add(new Role(ROLE_ADMIN));

        adminUser = new User("admin", "admin@gmail.com", "password");
        adminUser.setRoles(roles);
    }

    // Tests for when a registering user submits an already existent username
    @Test
    void AuthService_RegisterUser_DuplicateUsernameError(){
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignupRequest request = new SignupRequest("bobjoe", "bobjoe@gmail.com", roles, "password");
        when(userRepository.existsByUsername(Mockito.any(String.class))).thenReturn(true);
        AuthResponse response = authService.registerUser(request);
        assertEquals("Error: Username is already taken!", response.getMessage());
    }

    // Tests for when a registering user submits an already existent email
    @Test
    void AuthService_RegisterUser_DuplicateEmailError(){
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignupRequest request = new SignupRequest("bobjoe", "bobjoe@gmail.com", roles, "password");
        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(true);
        AuthResponse response = authService.registerUser(request);
        assertEquals("Error: Email is already in use!", response.getMessage());
    }

    // Tests for when a registering user is given the "user" role
    @Test
    void AuthService_RegisterUser_RolesSpecifiedUser(){
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignupRequest request = new SignupRequest("test", "test@gmail.com", roles, "password");
        when(roleRepository.findByName(Mockito.any(ERole.class))).thenReturn(userRole);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(encoder.encode(Mockito.anyString())).thenReturn("password");
        AuthResponse response = authService.registerUser(request);
        assertEquals("User registered successfully!", response.getMessage());
        assertTrue(response.getUser().getRoles().contains(new Role(ROLE_USER)));
        assertNotNull(response.getUser());
    }

    // Tests for when a registering user is given the "admin" role
    @Test
    void AuthService_RegisterUser_RolesSpecifiedAdmin(){
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        SignupRequest request = new SignupRequest("admin", "admin@gmail.com", roles, "password");
        when(roleRepository.findByName(Mockito.any(ERole.class))).thenReturn(adminRole);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(adminUser);
        when(encoder.encode(Mockito.anyString())).thenReturn("password");
        AuthResponse response = authService.registerUser(request);
        assertEquals("User registered successfully!", response.getMessage());
        assertTrue(response.getUser().getRoles().contains(new Role(ROLE_ADMIN)));
        assertNotNull(response.getUser());
    }

    // Tests for when a registering user was not given any roles
    @Test
    void AuthService_RegisterUser_RolesUnspecified(){
        SignupRequest request = new SignupRequest("test", "test@gmail.com", null, "password");
        when(roleRepository.findByName(Mockito.any(ERole.class))).thenReturn(userRole);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(encoder.encode(Mockito.anyString())).thenReturn("password");
        AuthResponse response = authService.registerUser(request);
        assertEquals("User registered successfully!", response.getMessage());
        assertTrue(response.getUser().getRoles().contains(new Role(ROLE_USER)));
        assertNotNull(response.getUser());
    }
}