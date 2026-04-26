package com.ctse.patient_service.service;

import com.ctse.patient_service.model.User;
import com.ctse.patient_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.ctse.patient_service.dto.UserDto;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    public PatientService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UserDto registerUser(UserDto userDto) {
        User user = new User();
        user.setPatientName(userDto.getPatientName());
        user.setContactNumber(userDto.getContactNumber());
        user.setAge(userDto.getAge());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
        user.setRole(userDto.getRole());
        
        User savedUser = userRepository.save(user);

        try {
            rabbitTemplate.convertAndSend("appointment-exchange", "user.registered", "User Registered: " + savedUser.getEmail());
        } catch (AmqpException e) {
            log.warn("Unable to publish user registration event to RabbitMQ: {}", e.getMessage());
        }

        return convertToDto(savedUser);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDto);
    }

    public Optional<UserDto> findById(String id) {
        return userRepository.findById(id).map(this::convertToDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(UserDto userDto) {
        Optional<User> existingUserOpt = userRepository.findById(userDto.getId());
        
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            String newPassword = userDto.getPasswordHash();
            String existingPassword = existingUser.getPasswordHash();
            
            // If password is different and not already encrypted, encrypt it
            if (newPassword != null && !newPassword.equals(existingPassword) && !newPassword.startsWith("$2a$") && !newPassword.startsWith("$2b$")) {
                existingUser.setPasswordHash(passwordEncoder.encode(newPassword));
            }
            
            existingUser.setPatientName(userDto.getPatientName());
            existingUser.setContactNumber(userDto.getContactNumber());
            existingUser.setAge(userDto.getAge());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setRole(userDto.getRole());
            
            return convertToDto(userRepository.save(existingUser));
        }
        throw new IllegalArgumentException("User not found");
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public Optional<UserDto> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .map(this::convertToDto);
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
