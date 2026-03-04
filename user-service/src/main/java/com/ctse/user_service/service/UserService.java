package com.ctse.user_service.service;

import com.ctse.user_service.model.User;
import com.ctse.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        // Check if password is being updated and encrypt it
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            String newPassword = user.getPasswordHash();
            String existingPassword = existingUser.get().getPasswordHash();
            
            // If password is different and not already encrypted, encrypt it
            if (newPassword != null && !newPassword.equals(existingPassword)) {
                // Check if it's already encrypted (BCrypt hashes start with $2a$ or $2b$)
                if (!newPassword.startsWith("$2a$") && !newPassword.startsWith("$2b$")) {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                }
            }
        }
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return user;
        }
        return Optional.empty();
    }
}
