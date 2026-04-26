package com.ctse.patient_service.controller;

import com.ctse.patient_service.dto.LoginRequest;
import com.ctse.patient_service.dto.LoginResponse;
import com.ctse.patient_service.dto.UserDto;
import com.ctse.patient_service.model.User;
import com.ctse.patient_service.service.PatientService;
import com.ctse.patient_service.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "User Management", description = "APIs for user registration, authentication, and management")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;
    private final JwtUtil jwtUtil;

    public PatientController(PatientService patientService, JwtUtil jwtUtil) {
        this.patientService = patientService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user account with patient, doctor, or admin role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<UserDto> registerUser( @RequestBody UserDto userDto) {
        log.info("Registering user: age={}, contact={}", userDto.getAge(), userDto.getContactNumber());
        User user = new User();
        user.setPatientName(userDto.getPatientName());
        user.setContactNumber(userDto.getContactNumber());
        user.setAge(userDto.getAge());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPasswordHash());
        user.setRole(userDto.getRole());
        
        User created = patientService.registerUser(user);
        return ResponseEntity.ok(convertToDto(created));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = patientService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (user.isPresent()) {
            User loggedInUser = user.get();
            // Generate JWT token
            String token = jwtUtil.generateToken(
                loggedInUser.getEmail(), 
                loggedInUser.getId(), 
                loggedInUser.getRole()
            );
            LoginResponse response = new LoginResponse(
                "Login successful",
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                loggedInUser.getRole(),
                token
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve list of all users (Doctors and Admins only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(patientService.getAllUsers().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable String id) {
        Optional<User> user = patientService.findById(id);
        return user.map(u -> ResponseEntity.ok(convertToDto(u))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get user profile", description = "Retrieve user profile by email",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<UserDto> getUserProfile(
            @Parameter(description = "User email", required = true) @RequestParam String email) {
        Optional<User> user = patientService.findByEmail(email);
        return user.map(u -> ResponseEntity.ok(convertToDto(u))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Update user", description = "Update user information",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable String id,
            @RequestBody UserDto userDto) {
        User user = new User();
        user.setId(id);
        user.setPatientName(userDto.getPatientName());
        user.setContactNumber(userDto.getContactNumber());
        user.setAge(userDto.getAge());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPasswordHash());
        user.setRole(userDto.getRole());
        
        User updated = patientService.updateUser(user);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user account (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable String id) {
        patientService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setPatientName(user.getPatientName());
        dto.setContactNumber(user.getContactNumber());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
